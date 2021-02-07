package com.zoomphant.agent.trace.common.rewrite;

import com.zoomphant.agent.trace.common.minimal.TraceLog;

public class ConsoleReporter implements SpanReporter{
    @Override
    public boolean start(Span s) {
        TraceLog.info("Started " + s);
        return true;
    }

    @Override
    public boolean finish(Span s) {
        TraceLog.info("Finished " + s);
        return true;
    }

    @Override
    public void abandon(Span s) {
        TraceLog.info("Abandon " + s);
    }
}
