package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.Checker;
import com.zoomphant.agent.trace.checker.DiscoveredInfo;
import com.zoomphant.agent.trace.checker.ProcInfo;
import com.zoomphant.agent.trace.checker.SQLChecker;
import com.zoomphant.agent.trace.common.AgentThread;
import com.zoomphant.agent.trace.common.TraceOption;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TraceMain {
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

                List<ProcInfo> procInfoList = ProcessUtils.allProcess();
                // for each process collect the informations...
                for (ProcInfo p : procInfoList) {
                    List<DiscoveredInfo> discoveredInfos = new ArrayList<>();
                    for (Checker c : allCheckers) {
                        DiscoveredInfo discoveredInfo = c.check(p);
                        if (discoveredInfo != null) {
                            discoveredInfos.add(discoveredInfo);
                            //            /Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar
                            String jar = new File(libsFolder, c.supportedTracers().name() + ".jar").getAbsolutePath();
                            Map<String, String> options = new HashMap<>();
                            options.put(TraceOption.HOST, "127.0.0.1");
                            options.put(TraceOption.PORT, HostServer.DEFAULT_PORT + "");
                            options.put(TraceOption.CENTRALHOST, "127.0.0.1");
                            options.put(TraceOption.CENTRALPORT, "9411");
                            Thread th = new Thread(new AttachTask(p.getId(), jar, options));
                            th.start();
                        }
                    }
                }
                while (true) {
                    try {
                        Thread.sleep(TimeUnit.MINUTES.toMillis(10));
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }).start();
    }
}
