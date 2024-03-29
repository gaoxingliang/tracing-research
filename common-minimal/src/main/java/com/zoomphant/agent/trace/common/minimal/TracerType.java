package com.zoomphant.agent.trace.common.minimal;

public enum TracerType {
    /**
     * Those cared normal jvm metrics attached and report by jmx
     */
    JMX_BASE("jmxbase", "jmx-base-0.0.1-all.jar", "com.zoomphant.agent.trace.jmx.JMXBaseMain"),

    /**
     * jvm which using jdbc
     */
    SQL_JAVA("sql", "sql-java-0.0.1-all.jar", "com.zoomphant.agent.trace.sql.SqlMain", "java.sql.Statement"),

    /**
     * kafka broker jvm
     */
    KAFKA_TRACE("kafkajmx", "kafka-trace-0.0.1-all.jar", "com.zoomphant.agent.trace.kafka.KafkaMain"),

    /**
     * ZK jvm
     */
    ZOOKEEPER_TRACE("zookeeperjmx", "zookeeper-trace-0.0.1-all.jar", "com.zoomphant.agent.trace.zookeeper.ZookeeperMain"),


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
