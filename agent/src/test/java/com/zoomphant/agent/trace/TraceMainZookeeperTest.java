package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.ZookeeperChecker;

import java.util.HashMap;
import java.util.Map;

public class TraceMainZookeeperTest {
    public static void main(String[] args) throws InterruptedException {
        Map<String, String> ids = new HashMap<>();
        ids.put("_resourceName", "hello");
        TraceMain.start("./releaselibs", new ZookeeperChecker(), ids);

        Thread.sleep(1000000);
    }


}
