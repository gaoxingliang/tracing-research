package com.zoomphant.agent.trace.common.minimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
public class ListOfSpans implements Serializable {

    private static final long serialVersionUID = 5830700363167253059L;

    @Getter
    @Setter
    List<Span> spanList;

}
