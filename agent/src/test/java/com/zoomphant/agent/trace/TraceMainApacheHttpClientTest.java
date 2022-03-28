package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.ApacheHttpClientChecker;

import java.util.HashMap;
import java.util.Map;

public class TraceMainApacheHttpClientTest {
    public static void main(String[] args) throws InterruptedException {
        // System.out.println(TracerType.valueOf("APACHE_HTTPCLIENT_JAVA"));

        TraceMain.functionalityEnabled = true;
        Map<String, String> ids = new HashMap<>();
        ids.put("_resourceName", "hello");
        TraceMain.testCmd = "demo-0.0.1-SNAPSHOT.jar";
        TraceMain.start("./releaselibs", new ApacheHttpClientChecker(), ids);

        Thread.sleep(1000000);
    }


}
