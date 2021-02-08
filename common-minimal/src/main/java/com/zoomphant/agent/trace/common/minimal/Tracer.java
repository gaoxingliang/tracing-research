package com.zoomphant.agent.trace.common.minimal;

import lombok.Getter;

public class Tracer {

    @Getter
    private final SpanReporter spanReporter;
    private String name;

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
