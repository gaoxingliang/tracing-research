package com.zoomphant.agent.trace.common.minimal;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ContainerDiscovery implements Serializable {


    private static final long serialVersionUID = -4081692896531645398L;

    @Getter
    @Setter
    String source;

    @Getter
    Map<ProcessType, Map<ProcessTypeLabel, String>> processTypeMap = new HashMap<>(2);

    public enum ProcessType implements Serializable {
        KAFKA
    }

    public enum ProcessTypeLabel implements Serializable {
        kafka_brokerid,
        kafka_clusterid
    }

}
