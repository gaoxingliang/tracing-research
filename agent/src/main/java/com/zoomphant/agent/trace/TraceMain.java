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
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class TraceMain {

    /**
     * this is used for test purpose.
     * When this is set. we only match process contains this command.
     */
    public static String testCmd = null;

    public static final String BOOTSTRAP_JAR = "bootstrap-0.0.1-all.jar";
    public static final String SPY_JAR = "spy-0.0.1-all.jar";

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
                                if (testCmd != null && !p.getCmd().contains(testCmd)) {
                                    continue;
                                }
                                List<DiscoveredInfo> discoveredInfos = new ArrayList<>();
                                DiscoveredInfo discoveredInfo = checker.check(p);
                                if (discoveredInfo != null) {
                                    TraceLog.info("Found discovered info by checker - " + checker + " with " + discoveredInfo);
                                    discoveredInfos.add(discoveredInfo);
                                    //            /Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar
                                    // The jar is on the physical host node
                                    String agentJarFinalPath = new File(libsFolder, checker.supportedTracers().getJar()).getCanonicalPath();
                                    String bootstrapFinalPath = new File(libsFolder, BOOTSTRAP_JAR).getCanonicalPath();
                                    String spyFinalPath = new File(libsFolder, SPY_JAR).getCanonicalPath();
                                    String [] agentBootstrapSpy = new String[] {checker.supportedTracers().getJar(), BOOTSTRAP_JAR, SPY_JAR};

                                    // it's it's a docker process
                                    if (p.getContainerId() != null) {
                                        // let's copy the jar file
                                        // We have to copy the jar when attaching a process which is in docker.
                                        File rootDir = new File("/proc/" + p.getId() + "/root");
                                        if (rootDir.exists()) {
                                            File f = Paths.get(rootDir.getAbsolutePath(), "tmp", "zpdir").toFile();
                                            f.mkdirs();
                                            for (String fileNeedCopy : agentBootstrapSpy) {
                                                copyFileFromHost2Docker(libsFolder, f.getAbsolutePath(), fileNeedCopy);
                                            }
                                            // This will be the dir which the process in docker can see this file....
                                            agentJarFinalPath = "/tmp/zpdir/" + agentBootstrapSpy[0];
                                            bootstrapFinalPath = "/tmp/zpdir/" + agentBootstrapSpy[1];
                                            spyFinalPath = "/tmp/zpdir/" + agentBootstrapSpy[2];
                                            TraceLog.info("Copied all files to remote " + rootDir);
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
                                    options.put(TraceOption.BOOTSTRAP_JAR, bootstrapFinalPath);
                                    options.put(TraceOption.SPY_JAR, spyFinalPath);
                                    options.put(TraceOption.TRACER_TYPE, checker.supportedTracers().name());
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


    private static void copyFileFromHost2Docker(String sourceDirInHost, String targetDirInDocker, String fileName) throws IOException {
        FileUtils.copyFile(new File(sourceDirInHost, fileName), new File(targetDirInDocker, fileName));
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
