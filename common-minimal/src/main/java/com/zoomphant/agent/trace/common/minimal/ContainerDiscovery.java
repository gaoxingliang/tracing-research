package com.zoomphant.agent.trace.common.minimal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ContainerDiscovery implements Serializable {


    private static final long serialVersionUID = -4081692896531645398L;

    String source;

    Map<ProcessTypeLabel, String> shareLabels = new HashMap<>(2);

    Map<ProcessType, Map<ProcessTypeLabel, String>> processTypeMap = new HashMap<>(2);

    public enum ProcessType implements Serializable {
        KAFKA
    }

    public enum ProcessTypeLabel implements Serializable {
        kafka_brokerid,
        kafka_clusterid,
        kafka_zkurl,
        container_id,
        hostname,
        pid,
        nodename,
        zk_mode, // standalone or replica
        zk_myid // id for zk replica mode
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


    public Map<ProcessTypeLabel, String> getShareLabels() {
        return shareLabels;
    }
}
