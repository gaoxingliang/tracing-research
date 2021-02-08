package com.zoomphant.agent.trace.common.minimal;


public class Tracer {

    private final SpanReporter spanReporter;
    private String name;

    public SpanReporter getSpanReporter() {
        return spanReporter;
    }

    public String getName() {
        return name;
    }

    public Tracer(String name, SpanReporter spanReporter) {
        this.name = name;
        this.spanReporter = spanReporter;
    }

    public Span newTrace() {
        Span s = new Span();
        s.setLocal(name);
        s.setId(System.currentTimeMillis() + "");
        return s;
    }
}
