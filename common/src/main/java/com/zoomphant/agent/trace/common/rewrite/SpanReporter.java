package com.zoomphant.agent.trace.common.rewrite;

public interface SpanReporter {
    boolean start(Span s);
    boolean finish(Span s);
    void abandon(Span s);
}
