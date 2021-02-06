package com.zoomphant.agent.trace.common;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContainerDiscoveryTest {
    public static void main(String[] args) {
        Map<ContainerDiscovery.ProcessTypeLabel, String> adMap = new HashMap<>(2);
        adMap.put(ContainerDiscovery.ProcessTypeLabel.kafka_clusterid, "abc");
        adMap.put(ContainerDiscovery.ProcessTypeLabel.kafka_brokerid, "efg");
        ContainerDiscovery containerDiscovery = new ContainerDiscovery();
        containerDiscovery.source = "hello";
        containerDiscovery.getProcessTypeMap().put(ContainerDiscovery.ProcessType.KAFKA, adMap);
        System.out.println(JSONObject.toJSONString(containerDiscovery));
        ContainerDiscovery r = JSONObject.parseObject(JSONObject.toJSONString(containerDiscovery), ContainerDiscovery.class);
        System.out.println(r.getProcessTypeMap());
    }
}
