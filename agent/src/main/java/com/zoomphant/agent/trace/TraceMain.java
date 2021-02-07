package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.Checker;
import com.zoomphant.agent.trace.checker.DiscoveredInfo;
import com.zoomphant.agent.trace.checker.KafkaChecker;
import com.zoomphant.agent.trace.checker.ProcInfo;
import com.zoomphant.agent.trace.checker.SQLChecker;
import com.zoomphant.agent.trace.common.AgentThread;
import com.zoomphant.agent.trace.common.ThreadUtils;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.minimal.TraceOption;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class TraceMain {

    public static final String BOOTSTRAP_JAR = "bootstrap-0.0.1-all.jar";
    public static Integer testPid;

    public static Set<String> alreadyEnabledChecker = new ConcurrentSkipListSet<>();
    public static Set<Long> alreadyAttachedProcces = new ConcurrentSkipListSet<>();
    /**
     * It will use below envs:
     * _ZP_ENV_NODE  : k8s108
     * CENTRAL_AGENT_SERVICE_SERVICE_HOST :  127.0.0.1
     *
     * @param libsFolder
     * @param checker which kind of tasks we want to detect
     * @param reportingProps this is used to generate the reporting props (eg mp id, mp product name... or others...)
     *
     */
    public static boolean start(final String libsFolder, Checker checker, final Map<String, String> reportingProps) {
        synchronized (alreadyEnabledChecker) {
            if (alreadyEnabledChecker.contains(checker.supportedTracers().getName())) {
                return false;
            }
            alreadyEnabledChecker.add(checker.supportedTracers().getName());

            new AgentThread("trace-main", new Runnable() {
                @Override
                public void run() {
                    /**
                     * first grep all processes
                     *
                     *
                     * Let's check every 5.... minutes
                     */
                    while (true) {
                        try {
                            TraceLog.info("Try checking for checker " + checker);
                            List<ProcInfo> procInfoList = ProcessUtils.allProcess2();
                            // for each process collect the informations...
                            for (ProcInfo p : procInfoList) {
                                if (alreadyAttachedProcces.contains(p.getId())) {
                                    continue;
                                }
                                if (testPid != null && testPid != p.getId()) {
                                    continue;
                                }
                                List<DiscoveredInfo> discoveredInfos = new ArrayList<>();
                                DiscoveredInfo discoveredInfo = checker.check(p);
                                if (discoveredInfo != null) {
                                    TraceLog.info("Found discovered info by checker - " + checker + " with " + discoveredInfo);
                                    discoveredInfos.add(discoveredInfo);
                                    //            /Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar
                                    // The jar is on the physical host node
                                    File agentJarFileOnHost = new File(libsFolder, checker.supportedTracers().getJar());
                                    String agentJarFinalPath = agentJarFileOnHost.getCanonicalPath();
                                    File bootstrapJarFileOnHost = new File(libsFolder, BOOTSTRAP_JAR);
                                    String bootstrapFinalPath = bootstrapJarFileOnHost.getCanonicalPath();
                                    // it's it's a docker process
                                    if (p.getContainerId() != null) {
                                        // let's copy the jar file
                                        // We have to copy the jar when attaching a process which is in docker.
                                        File rootDir = new File("/proc/" + p.getId() + "/root");
                                        if (rootDir.exists()) {
                                            File tmpDir = new File(rootDir, "tmp");
                                            if (!(tmpDir.exists() && tmpDir.isDirectory())) {
                                                tmpDir.mkdirs();
                                                TraceLog.info("mkir tmpdir " + tmpDir);
                                            }

                                            File f = Paths.get(tmpDir.getAbsolutePath(), "zpdir").toFile();
                                            if (!f.exists()) {
                                                f.mkdirs();
                                            }

                                            File agentJarFileOnDocker = new File(f, checker.supportedTracers().getJar());
                                            FileUtils.copyFile(agentJarFileOnHost, agentJarFileOnDocker);
                                            File bootstrapJarFileOnDocker = new File(f, BOOTSTRAP_JAR);
                                            FileUtils.copyFile(bootstrapJarFileOnHost, bootstrapJarFileOnDocker);
                                            TraceLog.debug("Copy file " + agentJarFileOnDocker.getCanonicalPath() + " and " + bootstrapJarFileOnDocker.getCanonicalPath());
                                            // This will be the dir which the process in docker can see this file....
                                            agentJarFinalPath = "/tmp/zpdir/" + agentJarFileOnDocker.getName();
                                            bootstrapFinalPath = "/tmp/zpdir/" + bootstrapJarFileOnDocker.getName();
                                        }
                                        else {
                                            TraceLog.info("Error. The container didn't have a proc root dir " + rootDir);
                                        }
                                    }

                                    Map<String, String> options = new HashMap<>();
                                    options.put(TraceOption.PID, p.getId() + "");
                                    options.put(TraceOption.CONTAINER, p.getContainerId());
                                    options.put(TraceOption.NODENAME, Optional.ofNullable(System.getenv("_ZP_ENV_NODE")).orElse("mocknode"));
                                    options.put(TraceOption.CENTRALHOST,
                                            Optional.ofNullable(System.getenv("CENTRAL_AGENT_SERVICE_SERVICE_HOST")).orElse("127.0.0.1"));
                                    options.put(TraceOption.CENTRALPORT, "9411");
                                    options.put(TraceOption.JARFILE, agentJarFinalPath);
                                    options.put(TraceOption.AGENTCLASS, checker.supportedTracers().getMainClass());
                                    options.putAll(TraceOption.buildReportingHeaders(reportingProps));
                                    Thread th = new Thread(new AttachTask(p.getId(), bootstrapFinalPath, TraceOption.renderOptions(options)));
                                    th.start();
                                    alreadyAttachedProcces.add(p.getId());
                                }
                            }
                        }
                        catch (Exception e) {
                            TraceLog.error("Fail to attach ", e);
                        }
                        finally {
                            if (ThreadUtils.sleepInterruptable(60 * 5)) {
                                return;
                            }
                        }
                    }
                }
            }).start();
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        /**
         * A helper test method
         */
        SQLChecker sql = new SQLChecker();
        KafkaChecker kafka = new KafkaChecker();
        Checker c = null;
        if (args[1].equals("sql")) {
            c = sql;
        } else if (args[1].equals("kafka")) {
            c = kafka;
        } else {
            throw new IllegalArgumentException("Unknown checker - " + args[0]);
        }
        TraceMain.start(args[0], c, new HashMap<>());
        Thread.sleep(100000);
    }

}
