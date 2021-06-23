package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.checker.GenericJVMChecker;
import com.zoomphant.agent.trace.common.minimal.TraceOption;

import java.util.HashMap;
import java.util.Map;

/**
 * test for the normal java
 */
public class TraceMainGenericJvmJavaTest {
    public static void main(String[] args) throws InterruptedException {

        Map<String, String> ids = new HashMap<>();
        ids.put("_resourceName", "hello");

        Map<String, String> options = new HashMap<>();
        options.put(TraceOption.NODENAME, "EdwardLocal");
        TraceMain.start("./releaselibs", new GenericJVMChecker(".*demo-0\\.0\\.1-SNAPSHOT\\.jar.*"), ids,
                options);
        Thread.sleep(1000000);
    }


}
