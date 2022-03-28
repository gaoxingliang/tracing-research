package com.zoomphant.agent.trace.common.minimal;

public interface SpanReporter {
    boolean start(Span s);
    boolean finish(Span s);
    void abandon(Span s);
    void stop();
}
