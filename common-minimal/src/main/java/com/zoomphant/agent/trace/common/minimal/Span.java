package com.zoomphant.agent.trace.common.minimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Span implements Serializable {

    private static final long serialVersionUID = 2702050515222067762L;
    private String id;
    private String name;
    private String local;
    private String remote;
    @Builder.Default
    private Map<String, String> tags = new HashMap<>(4);
    private long startNano;
    private long endNano;
    @Builder.Default
    private boolean isError = false;

    public Span(){}

    public void start() {
        startNano = System.nanoTime();
    }

    public void finish() {
        endNano = System.nanoTime();
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
