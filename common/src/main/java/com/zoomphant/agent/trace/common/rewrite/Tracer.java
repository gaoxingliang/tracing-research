package com.zoomphant.agent.trace.common.rewrite;

public class Tracer {

    private final SpanReporter spanReporter;
    private String name;

    public Tracer(String name, SpanReporter spanReporter) {
        this.name = name;
        this.spanReporter = spanReporter;
    }

    public Span newTrace() {
        Span s = new Span();
        s.setSpanReporter(spanReporter);
        s.setId(System.currentTimeMillis() + "");
        return s;
    }
}
