package com.zoomphant.agent.trace.common.minimal;

public enum TracerType {
    /**
     * jvm which using jdbc
     */
    SQL("sql", "sql-trace-0.0.1-all.jar", "com.zoomphant.agent.trace.sql.SqlMain"),

    /**
     * kafka broker jvm
     */
    KAFKA_JMX("kafkajmx", "kafka-trace-0.0.1-all.jar", "com.zoomphant.agent.trace.kafka.KafkaMain"),

    /**
     * jvm which using kafka clients
     */
    KAFKA_JAVA("kafkajava", "kafka-java-0.0.1-all.jar", "com.zoomphant.agent.trace.kafkajava.KafkaJavaMain",
            "org.apache.kafka.clients.producer.KafkaProducer"),

    /**
     * jvm which using apache httpclients (not async http clients)
     */
    APACHE_HTTPCLIENT_JAVA("apachehttpclient_java", "apache-httpclient-java-0.0.1-all.jar", "com.zoomphant.agent.trace.apache.httpclient.HttpMain",
            "org.apache.http.client.HttpClient");


    private String name;

    private String jar;

    private String mainClass;

    // possible null.
    // We used this to do conditional attaching.
    // eg we need to check whether the jvm used kafka related library and then we continue to attach.
    private String requiredClass;

    TracerType(String name, String jar, String mainClass) {
        this(name, jar, mainClass, "");
    }

    TracerType(String name, String jar, String mainClass, String requiredClass) {
        this.name = name;
        this.jar = jar;
        this.mainClass = mainClass;
        this.requiredClass = requiredClass;
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

    public String getRequiredClass() {
        return requiredClass;
    }
}
