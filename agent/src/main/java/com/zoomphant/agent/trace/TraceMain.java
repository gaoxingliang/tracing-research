package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.Checker;
import com.zoomphant.agent.trace.checker.DiscoveredInfo;
import com.zoomphant.agent.trace.checker.ProcInfo;
import com.zoomphant.agent.trace.checker.SQLChecker;
import com.zoomphant.agent.trace.common.AgentThread;
import com.zoomphant.agent.trace.common.ThreadUtils;
import com.zoomphant.agent.trace.common.TraceLog;
import com.zoomphant.agent.trace.common.TraceOption;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TraceMain {

    /**
     * It will use below envs:
     *    _ZP_ENV_NODE  : k8s108
     *    CENTRAL_AGENT_SERVICE_SERVICE_HOST :  127.0.0.1
     *
     *
     * @param libsFolder
     */
    public static void start(final String libsFolder) {
        List<Checker> allCheckers = Arrays.asList(new SQLChecker());
        new AgentThread("trace-main", new Runnable() {
            @Override
            public void run() {
                /**
                 * first grep all processes
                 *
                 *
                 * Let's check every 10.... minutes
                 */
                Set<Long> alreadyAttached = new HashSet<>();
                try {
                    List<ProcInfo> procInfoList = ProcessUtils.allProcess();
                    // for each process collect the informations...
                    for (ProcInfo p : procInfoList) {
                        if (alreadyAttached.contains(p.getId())) {
                            continue;
                        }
                        List<DiscoveredInfo> discoveredInfos = new ArrayList<>();
                        for (Checker c : allCheckers) {
                            DiscoveredInfo discoveredInfo = c.check(p);
                            if (discoveredInfo != null) {
                                discoveredInfos.add(discoveredInfo);
                                //            /Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar
                                String jar = new File(libsFolder, c.supportedTracers().getJar()).getCanonicalPath();
                                Map<String, String> options = new HashMap<>();
                                options.put(TraceOption.HOST, "127.0.0.1");
                                options.put(TraceOption.PORT, HostServer.DEFAULT_PORT + "");
                                options.put(TraceOption.PID, p.getId() + "");
                                options.put(TraceOption.CONTAINER, p.getContainerId());
                                options.put(TraceOption.NODENAME, Optional.ofNullable(System.getenv("_ZP_ENV_NODE")).orElse("mocknode"));
                                options.put(TraceOption.CENTRALHOST, Optional.ofNullable(System.getenv("CENTRAL_AGENT_SERVICE_SERVICE_HOST")).orElse("127.0.0.1"));
                                options.put(TraceOption.CENTRALPORT, "9411");
                                Thread th = new Thread(new AttachTask(p.getId(), jar, options));
                                th.start();
                                alreadyAttached.add(p.getId());
                            }
                        }
                    }
                } catch (Exception e) {
                    TraceLog.error("Fail to attach ", e);
                } finally {
                    if (ThreadUtils.sleepInterruptable(60 * 10)) {
                        return;
                    }
                }
            }
        }).start();
    }
}
