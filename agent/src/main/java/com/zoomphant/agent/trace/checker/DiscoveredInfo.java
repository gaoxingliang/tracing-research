package com.zoomphant.agent.trace.checker;


import java.util.Map;

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

    public static DiscoveredInfo newZk() {
        DiscoveredInfo discoveredInfo = new DiscoveredInfo();
        discoveredInfo.type = "zookeeper";
        return discoveredInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    @Override
    public String toString() {
        return "DiscoveredInfo{" +
                "type='" + type + '\'' +
                ", props=" + props +
                '}';
    }
}
