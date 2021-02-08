package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.minimal.utils.VMUtil;

public class AttachTask implements Runnable {

    private final long pid;
    private final String jarFile;
    private final String args;
    public AttachTask(long pid, String jarFile, String args) {
        this.pid = pid;
        this.jarFile = jarFile;
        this.args = args;
    }


    @Override
    public void run() {
        TraceLog.info("Starting attaching " + pid + " with args " + args);
        try {
            VMUtil.attach(pid + "", jarFile, args);
        } catch (Throwable e) {
            TraceLog.error("Fail to attaching / processing attach pid " + pid, e);
        }
    }

}
