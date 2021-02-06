package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.common.VMUtil;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.minimal.TraceOption;

import java.util.Map;

public class AttachTask implements Runnable {

    private final long pid;
    private final String jarFile;
    private final Map<String, String> options;
    public AttachTask(long pid, String jarFile, Map<String, String> options) {
        this.pid = pid;
        this.jarFile = jarFile;
        this.options = options;
    }


    @Override
    public void run() {
        TraceLog.info("Starting attaching " + pid);
        try {
            VMUtil.attach(pid + "", jarFile, TraceOption.renderOptions(options));
        } catch (Throwable e) {
            TraceLog.error("Fail to attaching / processing attach pid " + pid, e);
        }
    }

}
