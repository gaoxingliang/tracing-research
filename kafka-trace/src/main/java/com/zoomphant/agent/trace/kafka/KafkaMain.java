package com.zoomphant.agent.trace.kafka;

import com.zoomphant.agent.trace.common.BasicMain;
import com.zoomphant.agent.trace.common.JMXMain;
import com.zoomphant.agent.trace.common.JmxUtils;
import com.zoomphant.agent.trace.common.TraceLog;
import com.zoomphant.agent.trace.common.TracerType;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaMain extends JMXMain {

    @Override
    protected String getConfigYaml() {
        String name = "kafka.server:type=app-info";
        String version = JmxUtils.getValue(name, "version");
        return version.startsWith("2.") ? "kafka2_0_JMX.yaml" :"kafkaJmx.yaml";
    }

    @Override
    protected Map<String, String> getOtherReportingHeaders() {
        /**
         * for kafka add the cluster id and broker Id
         */
        Map<String, String> map = new HashMap<>(2);
        List<String> ids = JmxUtils.getNodes("kafka.server:id=*,type=app-info", "id");
        if (!ids.isEmpty()) {
            map.put("kafka_brokerid", ids.get(0));
        }
        String clusterId = JmxUtils.getValue("kafka.server:type=KafkaServer,name=ClusterId", "Value");
        map.put("kafka_clusterid", clusterId);
        return map;
    }

    private static void install(String agentArgs, Instrumentation inst) {
        TraceLog.info("Will install kafka with " + agentArgs);
        /**
         * Starts a thread
         */
        KafkaMain main = new KafkaMain();
        if (!main.start(TracerType.KAFKA_JMX, agentArgs, inst)) {
            return;
        }
        BasicMain.HOLDER.put(TracerType.KAFKA_JMX, main);
        TraceLog.info("Kafka main installed");
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        install(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        install(agentArgs, inst);
    }
}

