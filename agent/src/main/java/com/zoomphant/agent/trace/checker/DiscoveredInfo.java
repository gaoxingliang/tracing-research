package com.zoomphant.agent.trace.checker;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@ToString
@Data
public class DiscoveredInfo {
    String type;
    Map<String, Object> props;


    public static DiscoveredInfo newJava() {
        DiscoveredInfo discoveredInfo = new DiscoveredInfo();
        discoveredInfo.type = "java";
        return discoveredInfo;
    }

    public static DiscoveredInfo newKafka() {
        DiscoveredInfo discoveredInfo = new DiscoveredInfo();
        discoveredInfo.type = "kafka";
        return discoveredInfo;
    }

}
