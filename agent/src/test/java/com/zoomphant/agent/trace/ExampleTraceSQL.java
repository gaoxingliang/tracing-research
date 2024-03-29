package com.zoomphant.agent.trace;

import com.zoomphant.agent.trace.common.minimal.TraceOption;
import com.zoomphant.agent.trace.common.minimal.TracerType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ExampleTraceSQL {


    public static void main(String[] args) throws Exception {
        // HostServer.start(HostServer.DEFAULT_PORT);
        String x = "execute|executeUpdate|executeQuery";
        System.out.println(Pattern.matches(x, "executeQuery"));

        long pid = 7464;
        //            /Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar
        String jar = new File("./releaselibs/sql-java-0.0.1-all.jar").getCanonicalPath();
        Map<String, String> options = new HashMap<>();
        options.put(TraceOption.CENTRALHOST, "127.0.0.1");
        options.put(TraceOption.CENTRALPORT, "9411");
        options.put(TraceOption.CONTAINER, "MOCKED");
        options.put(TraceOption.JARFILE, jar);
        options.put(TraceOption.TRACER_TYPE, TracerType.SQL_JAVA.name());
        String boostjar = new File("./releaselibs/" + TraceMain.BOOTSTRAP_JAR).getCanonicalPath();

        Thread th = new Thread(new AttachTask(pid, jar, TraceOption.renderOptions(options)));
        th.start();

        // _centralhost=127.0.0.1##_host=127.0.0.1##_centralport=9411##_port=19234
        Thread.sleep(1000000);
    }
}
