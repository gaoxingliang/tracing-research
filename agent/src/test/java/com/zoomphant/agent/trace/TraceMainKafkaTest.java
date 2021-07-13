package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.KafkaChecker;
import com.zoomphant.agent.trace.common.minimal.TraceOption;

import java.util.HashMap;
import java.util.Map;

public class TraceMainKafkaTest {
    public static void main(String[] args) throws InterruptedException {
        Map<String, String> ids = new HashMap<>();
        ids.put("_resourceName", "hello");
        ids.put(TraceOption.REPORTING_HEADER_PREFIX + "mpid", "11117890");

        TraceMain.start("./releaselibs", new KafkaChecker(), ids);

        Thread.sleep(1000000);
    }


}
