package com.zoomphant.agent.trace.kafka;

import com.zoomphant.agent.trace.common.BasicMain;
import com.zoomphant.agent.trace.common.JMXMain;
import com.zoomphant.agent.trace.common.JmxUtils;
import com.zoomphant.agent.trace.common.TraceLog;
import com.zoomphant.agent.trace.common.TracerType;

import java.lang.instrument.Instrumentation;

public class KafkaMain extends JMXMain {

    @Override
    protected String getConfigYaml() {
        String name = "kafka.server:type=app-info";
        String version = JmxUtils.getValue(name, "version");
        return version.startsWith("2.") ? "kafka2_0_JMX.yaml" :"kafkaJmx.yaml";
    }

    private static void install(String agentArgs, Instrumentation inst) {
        TraceLog.info("Will install kafka with " + agentArgs);
        /**
         * Starts a thread
         */
        KafkaMain main = new KafkaMain();
        main.start(TracerType.KAFKA_JMX, agentArgs, inst);
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

