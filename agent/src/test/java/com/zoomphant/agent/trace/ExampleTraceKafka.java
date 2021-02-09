package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.AttachTask;
import com.zoomphant.agent.trace.common.minimal.TraceOption;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ExampleTraceKafka {


    public static void main(String[] args) throws Exception {

        long pid = 38206;
        //            /Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar
        String jar = new File("./releaselibs/kafka-trace-0.0.1-all.jar").getCanonicalPath();
        Map<String, String> options = new HashMap<>();
        options.put(TraceOption.CENTRALHOST, "127.0.0.1");
        options.put(TraceOption.CENTRALPORT, "9411");
        options.put(TraceOption.CONTAINER, "MOCKED");

        options.put(TraceOption.REPORTING_HEADER_PREFIX + "mpid", "123456");

        Thread th = new Thread(new AttachTask(pid, jar, TraceOption.renderOptions(options)));
        th.start();

        // _centralhost=127.0.0.1##_host=127.0.0.1##_centralport=9411##_port=19234
        Thread.sleep(1000000);
    }
}
