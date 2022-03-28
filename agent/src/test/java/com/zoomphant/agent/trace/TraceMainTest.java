package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.SQLChecker;

import java.util.HashMap;
import java.util.Map;

public class TraceMainTest {
    public static void main(String[] args) throws InterruptedException {
        Map<String, String> ids = new HashMap<>();
        ids.put("_resourceName", "hello");
        TraceMain.testCmd = "demo-0.0.1-SNAPSHOT.jar";
        TraceMain.functionalityEnabled = true;
        TraceMain.start("./releaselibs", new SQLChecker(), ids);

        Thread.sleep(10000);

        TraceMain.stop(new SQLChecker());
        Thread.sleep(10000);
        System.out.println(".....done");
    }


}
