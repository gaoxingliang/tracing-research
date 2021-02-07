package com.zoomphant.agent.trace.common.rewrite;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Span {
    private String id;
    private String name;
    private String local;
    private String remote;
    @Builder.Default
    private Map<String, String> tags = new HashMap<>(4);
    private SpanReporter spanReporter;
    private long startNano;
    private long endNano;
    @Builder.Default
    private boolean isError = false;

    public Span(){}

    public void start() {
        startNano = System.nanoTime();
        spanReporter.start(this);
    }

    public void finish() {
        endNano = System.nanoTime();
        spanReporter.finish(this);
    }

    public Span error() {
        isError = true;
        return this;
    }

    public Span tag(String s, Object o) {
        tags.put(s, String.valueOf(o));
        return this;
    }
}
