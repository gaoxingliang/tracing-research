package com.zoomphant.agent.trace.common;

import io.prometheus.jmx.shaded.io.prometheus.jmx.JmxCollector;

public class JmxTest {
    public static void main(String[] args) throws Exception {
        String filePath = "kafka2_0_JMX.yaml";
        String fileContent = FileUtils.getFile(filePath);
        String body = "jmxUrl: service:jmx:rmi:///jndi/rmi://:9999/jmxrmi\n" + fileContent;


        JmxCollector jmxCollector = new JmxCollector(body);
        jmxCollector.collect().forEach(l -> System.out.println(l));

        Thread.sleep(30000);
        jmxCollector.collect().forEach(l -> System.out.println(l));
    }

}
