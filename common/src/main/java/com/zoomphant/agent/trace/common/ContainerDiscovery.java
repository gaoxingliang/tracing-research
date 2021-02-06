package com.zoomphant.agent.trace.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ContainerDiscovery {

    @Getter
    @Setter
    String source;

    @Getter
    Map<ProcessType, Map<ProcessTypeLabel, String>> processTypeMap = new HashMap<>(2);

    public enum ProcessType {
        KAFKA
    }

    public enum ProcessTypeLabel {
        kafka_brokerid,
        kafka_clusterid
    }
}
