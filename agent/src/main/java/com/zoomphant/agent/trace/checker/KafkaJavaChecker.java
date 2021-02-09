package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.minimal.TracerType;

public class KafkaJavaChecker extends JavaChecker {

    @Override
    public TracerType supportedTracers() {
        return TracerType.KAFKA_JAVA;
    }

}
