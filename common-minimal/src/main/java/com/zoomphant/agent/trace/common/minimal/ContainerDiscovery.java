package com.zoomphant.agent.trace.common.minimal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ContainerDiscovery implements Serializable {


    private static final long serialVersionUID = -4081692896531645398L;

    String source;

    Map<ProcessType, Map<ProcessTypeLabel, String>> processTypeMap = new HashMap<>(2);

    public enum ProcessType implements Serializable {
        KAFKA
    }

    public enum ProcessTypeLabel implements Serializable {
        kafka_brokerid,
        kafka_clusterid
    }

    public String getSource() {
        return source;
    }

    public Map<ProcessType, Map<ProcessTypeLabel, String>> getProcessTypeMap() {
        return processTypeMap;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setProcessTypeMap(Map<ProcessType, Map<ProcessTypeLabel, String>> processTypeMap) {
        this.processTypeMap = processTypeMap;
    }
}
