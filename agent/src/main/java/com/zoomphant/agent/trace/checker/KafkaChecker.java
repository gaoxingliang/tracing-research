package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.minimal.TracerType;

/**
 * check kafka broker jvm processes
 */
public class KafkaChecker extends JavaChecker{

    @Override
    public DiscoveredInfo _check(ProcInfo procInfo) {
        if (procInfo.getCmd().contains("kafka.Kafka")) {
            return DiscoveredInfo.newKafka();
        }
        return null;
    }

    @Override
    public TracerType supportedTracers() {
        return TracerType.KAFKA_TRACE;
    }
}
