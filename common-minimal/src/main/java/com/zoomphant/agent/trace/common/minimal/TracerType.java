package com.zoomphant.agent.trace.common.minimal;

public enum TracerType {
    SQL("sql", "sql-trace-0.0.1-all.jar", "com.zoomphant.agent.trace.sql.SqlMain"),
    KAFKA_JMX("kafkajmx", "kafka-trace-0.0.1-all.jar", "com.zoomphant.agent.trace.kafka.KafkaMain");

    private String name;

    private String jar;

    private String mainClass;

    TracerType(String name, String jar, String mainClass) {
        this.name = name;
        this.jar = jar;
        this.mainClass = mainClass;
    }

    public String getName() {
        return name;
    }

    public String getJar() {
        return jar;
    }

    public String getMainClass() {
        return mainClass;
    }
}
