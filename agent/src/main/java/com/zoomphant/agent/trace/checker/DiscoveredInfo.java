package com.zoomphant.agent.trace.checker;

import lombok.Data;

import java.util.Map;

@Data
public class DiscoveredInfo {
    String type;
    Map<String, Object> props;


    public static DiscoveredInfo newJava() {
        DiscoveredInfo discoveredInfo = new DiscoveredInfo();
        discoveredInfo.type = "java";
        return discoveredInfo;
    }
}
