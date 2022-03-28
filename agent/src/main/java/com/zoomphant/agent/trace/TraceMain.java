package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.*;
import com.zoomphant.agent.trace.common.minimal.*;
import org.apache.commons.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class TraceMain {

    /**
     * Let's control it whether this function is enable or not.
     * if it's not enable, we will not start any trace functions.
     */
    public static boolean functionalityEnabled = false; // default to false

    /**
     * this is used for test purpose.
     * When this is set. we only match process contains this command.
     */
    public static String testCmd = null;

    public static final String BOOTSTRAP_JAR = "bootstrap-0.0.1-all.jar";
    public static final String SPY_JAR = "spy-0.0.1-all.jar";
    public static final String BYTEBUDDY_SHARE_JAR = "bytebuddy-0.0.1-all.jar";


    public static Set<String> alreadyEnabledChecker = new ConcurrentSkipListSet<>();
    public static Map<Long /*pid*/, ConcurrentHashMap<String/*checker tracer  name*/, Map<String, String> /*options of this report*/>> alreadyAttachedProcces =
            new ConcurrentHashMap<>();

    public static boolean start(final String libsFolder, Checker checker, final Map<String, String> reportingProps) {
        return start(libsFolder, checker, reportingProps, new HashMap<>(0));
    }


    public static void stop(Checker checker) {
        synchronized (alreadyEnabledChecker) {
            if (!alreadyEnabledChecker.contains(checker.supportedTracers().getName())) {
                return;
            }
            final String tracerName = checker.supportedTracers().getName();
            alreadyAttachedProcces.forEach((pid, checker2options) -> {
                if (!checker2options.containsKey(tracerName)) {
                    return;
                }

                Map<String, String> options = checker2options.get(tracerName);
                options.put(TraceOption.COMMAND, TraceOption.COMMAND_STOP);
                Thread th = new Thread(new AttachTask(pid, TraceOption.getOption(options, TraceOption.BOOTSTRAP_JAR), TraceOption.renderOptions(options)));
                th.start();
                checker2options.remove(tracerName);
            });
        }
    }


    /**
     * It will use below envs:
     * _ZP_ENV_NODE  : k8s108
     * CENTRAL_AGENT_SERVICE_SERVICE_HOST :  127.0.0.1
     *
     * @param libsFolder     where the folders of the libs.
     * @param checker        which kind of tasks we want to detect
     * @param reportingProps this is used to generate the reporting props (eg mp id, mp product name... or others...)
     * @param traceOptions   additional Options (This has a higher priority than normal options) which will override the calculated options.
     *                       eg: you can decide the reported to host by overrides the key: CENTRALHOST {@link TraceOption#CENTRALHOST}
     */
    public static boolean start(final String libsFolder, Checker checker, final Map<String, String> reportingProps, final Map<String,
            String> traceOptions) {

        if (!functionalityEnabled) {
            TraceLog.info("The Trace functionality is disabled on collector.");
            return false;
        }

        /**
         * To avoid problem:
         *  which may cause problem if we try to attach the zp agent (self attach self.)
         */
        long currentProcessId = ProcessUtils.currentProcessId();

        synchronized (alreadyEnabledChecker) {
            if (alreadyEnabledChecker.contains(checker.supportedTracers().getName())) {
                return false;
            }
            final String tracerName = checker.supportedTracers().getName();
            alreadyEnabledChecker.add(tracerName);

            try {
                TraceLog.info("Try checking for checker " + checker);
                List<ProcInfo> procInfoList = ProcessUtils.allProcess2();
                int total = procInfoList.size();
                int skipped = 0, attached = 0;
                // for each process collect the informations...
                for (ProcInfo p : procInfoList) {
                    if (p.getId() == currentProcessId) {
                        skipped++;
                        continue;
                    }

                    ConcurrentHashMap<String, Map<String, String>> checker2options = alreadyAttachedProcces.computeIfAbsent(p.getId(), k -> new ConcurrentHashMap<>());
                    if (checker2options.containsKey(tracerName)) {
                        skipped++;
                        continue;
                    }
                    if (testCmd != null && !p.getCmd().contains(testCmd)) {
                        skipped++;
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
                        String bytebuddyJarFinalPath = new File(libsFolder, BYTEBUDDY_SHARE_JAR).getCanonicalPath();
                        String[] agentBootstrapSpy = new String[]{
                                checker.supportedTracers().getJar(),
                                BOOTSTRAP_JAR,
                                SPY_JAR, BYTEBUDDY_SHARE_JAR};

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
                                bytebuddyJarFinalPath = "/tmp/zpdir/" + agentBootstrapSpy[3];
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
                        options.put(TraceOption.BYTE_BUDDY_SHARE_JAR, bytebuddyJarFinalPath);
                        options.put(TraceOption.TRACER_TYPE, checker.supportedTracers().name());
                        options.put(TraceOption.COMMAND, TraceOption.COMMAND_START);
                        options.putAll(traceOptions);
                        options.putAll(TraceOption.buildReportingHeaders(reportingProps));
                        Thread th = new Thread(new AttachTask(p.getId(), bootstrapFinalPath, TraceOption.renderOptions(options)));
                        th.start();
                        checker2options.put(tracerName, options);
                        attached++;
                    }
                    else {
                        skipped++;
                    }
                }
                TraceLog.info(String.format("Process checking stats - total=%d,skipped=%d,attached=%d", total, skipped, attached));
            }
            catch (Exception e) {
                TraceLog.error("Fail to attach ", e);
            }
        }
        return true;
    }


    private static void copyFileFromHost2Docker(String sourceDirInHost, String targetDirInDocker, String fileName) throws IOException {
        // make sure the md5 is different
        // and then we copy
        File target = new File(targetDirInDocker, fileName);
        File src = new File(sourceDirInHost, fileName);
        if (target.exists()) {
            try {
                String md5Exists = "", md5New = "";
                try (InputStream is = Files.newInputStream(target.toPath())) {
                    md5Exists = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
                }
                try (InputStream is = Files.newInputStream(src.toPath())) {
                    md5New = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
                }
                if (md5Exists.equals(md5New)) {
                    TraceLog.info("same file, ignored " + fileName);
                }
            }
            catch (Exception e) {
            }
        }

        FileUtils.copyFile(src, target);
    }

}
