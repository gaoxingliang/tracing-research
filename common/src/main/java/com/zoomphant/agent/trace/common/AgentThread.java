package com.zoomphant.agent.trace.common;

import com.zoomphant.agent.trace.common.minimal.TraceLog;

public class AgentThread extends Thread{
    private final Runnable r;
    protected volatile boolean stopped = false;
    public AgentThread(String name, Runnable r) {
        setName(name);
        setDaemon(true);
        this.r = r;
    }

    public void stopIt() {
        stopped = true;
    }

    @Override
    public void run() {
        try {
            this.r.run();
        } catch (Throwable e) {
            TraceLog.error("Fail to run ", e);
        }
    }
}
