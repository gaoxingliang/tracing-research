package com.zoomphant.agent.trace.common.minimal;

import java.io.Serializable;
import java.util.List;

public class ListOfSpans implements Serializable {

    private static final long serialVersionUID = 5830700363167253059L;

    List<Span> spanList;

    public List<Span> getSpanList() {
        return spanList;
    }

    public void setSpanList(List<Span> spanList) {
        this.spanList = spanList;
    }
}
