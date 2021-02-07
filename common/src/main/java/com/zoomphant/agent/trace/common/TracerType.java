package com.zoomphant.agent.trace.common;

import lombok.Getter;

public enum TracerType {
    SQL("sql", "sql-trace-0.0.1-all.jar", "com.zoomphant.agent.trace.sql.SqlMain"),
    KAFKA_JMX("kafkajmx", "kafka-trace-0.0.1-all.jar", "com.zoomphant.agent.trace.kafka.KafkaMain");

    @Getter
    private String name;

    @Getter
    private String jar;

    @Getter
    private String mainClass;

    TracerType(String name, String jar, String mainClass) {
        this.name = name;
        this.jar = jar;
        this.mainClass = mainClass;
    }

}
