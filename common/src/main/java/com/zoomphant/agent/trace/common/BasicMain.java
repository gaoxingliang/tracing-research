package com.zoomphant.agent.trace.common;

import lombok.Getter;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicMain {

    @Getter
    protected Recorder recorder;
    @Getter
    protected Map<String, String> options;

    protected void start(String svcName, String agentArgs, Instrumentation inst) {
        options = TraceOption.parseOptions(agentArgs);
        final String host = TraceOption.getOption(options, TraceOption.HOST);
        final int port = TraceOption.getOptionInt(options, TraceOption.PORT);
        final String chost = TraceOption.getOption(options, TraceOption.CENTRALHOST);
        final int cport = TraceOption.getOptionInt(options, TraceOption.CENTRALPORT);
        recorder = new Recorder(svcName, String.format("http://%s:%d/api/v2", chost, cport));
    }



    public static final String SQL = "sql";
    public static final ConcurrentHashMap<String, BasicMain> HOLDER = new ConcurrentHashMap<>();
}
