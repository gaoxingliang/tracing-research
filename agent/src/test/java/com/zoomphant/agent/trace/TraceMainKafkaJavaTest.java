package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.KafkaJavaChecker;

import java.util.HashMap;
import java.util.Map;

/**
 * test for the kafka java
 */
public class TraceMainKafkaJavaTest {
    public static void main(String[] args) throws InterruptedException {

        Map<String, String> ids = new HashMap<>();
        ids.put("_resourceName", "hello");
        TraceMain.testCmd = "demo-0.0.1-SNAPSHOT.jar";
        TraceMain.start("./releaselibs", new KafkaJavaChecker(), ids);
        Thread.sleep(1000000);
    }


}
