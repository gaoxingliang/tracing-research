package com.zoomphant.agent.trace.common;

import lombok.Getter;

public enum TracerType {
    SQL("sql", "sql-trace-0.0.1-all.jar"),
    KAFKA_JMX("kafkajmx", "kafka-trace-0.0.1-all.jar");

    @Getter
    private String name;

    @Getter
    private String jar;

    TracerType(String name, String jar) {
        this.name = name;
        this.jar = jar;
    }

}
