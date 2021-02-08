package com.zoomphant.agent.trace.common.minimal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Span implements Serializable {

    private static final long serialVersionUID = 2702050515222067762L;
    private String id;
    private String name;
    private String local;
    private String remote;
    private Map<String, String> tags = new HashMap<>(4);
    private long startNano;
    private long endNano;
    private boolean isError = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public long getStartNano() {
        return startNano;
    }

    public void setStartNano(long startNano) {
        this.startNano = startNano;
    }

    public long getEndNano() {
        return endNano;
    }

    public void setEndNano(long endNano) {
        this.endNano = endNano;
    }

    public boolean isError() {
        return isError;
    }

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
