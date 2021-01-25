package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.common.TraceLog;
import com.zoomphant.agent.trace.common.TraceOption;
import com.zoomphant.agent.trace.common.VMUtil;

import java.util.HashMap;
import java.util.Map;

public class AttachTask implements Runnable {

    private final String pid;
    private final String jarFile;
    private final Map<String, String> options;
    public AttachTask(String pid, String jarFile, Map<String, String> options) {
        this.pid = pid;
        this.jarFile = jarFile;
        this.options = options;
    }


    @Override
    public void run() {
        TraceLog.info("Starting attaching " + pid);
        try {
            VMUtil.attach(pid, jarFile, buildOptions());
        } catch (Throwable e) {
            TraceLog.error("Fail to attaching / processing attach pid " + pid, e);
        }
    }

    private String buildOptions() {
        Map<String, String> allOptions = new HashMap<>();
        allOptions.put(TraceOption.HOST, "127.0.0.1");
        allOptions.put(TraceOption.PORT, HostServer.DEFAULT_PORT + "");
        if (options != null) {
            allOptions.putAll(options);
        }
        return TraceOption.renderOptions(allOptions);
    }

}
