package com.zoomphant.agent.trace.common;

import io.prometheus.jmx.shaded.io.prometheus.jmx.JmxCollector;
import lombok.Cleanup;

import java.io.InputStream;

public class JmxTest {
    public static void main(String[] args) throws Exception {
        String filePath = "kafkaExample.yaml";
        ClassLoader classLoader = JmxTest.class.getClassLoader();
        @Cleanup InputStream inputStream = classLoader.getResourceAsStream(filePath);
        String fileContent = FileUtils.getFile(filePath);
        JmxCollector jmxCollector = new JmxCollector(fileContent);
        jmxCollector.collect().forEach(l -> System.out.println(l));
    }

}
