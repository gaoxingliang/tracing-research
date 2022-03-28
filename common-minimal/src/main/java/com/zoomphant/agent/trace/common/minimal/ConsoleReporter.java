package com.zoomphant.agent.trace.common.minimal;

public class ConsoleReporter implements SpanReporter {
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

    @Override
    public void stop() {
        TraceLog.info("Stopped ConsoleReporter");
    }
}
