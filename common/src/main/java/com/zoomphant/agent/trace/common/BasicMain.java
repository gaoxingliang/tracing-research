package com.zoomphant.agent.trace.common;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicMain {

    @Getter
    protected Recorder recorder;
    @Getter
    protected Map<String, String> options;

    protected void start(TracerType tracer, String agentArgs, Instrumentation inst) {
        options = TraceOption.parseOptions(agentArgs);
        final String chost = TraceOption.getOption(options, TraceOption.CENTRALHOST);
        final int cport = TraceOption.getOptionInt(options, TraceOption.CENTRALPORT);
        String containerName = TraceOption.getOption(options, TraceOption.CONTAINER);
        String source;
        if (StringUtils.isEmpty(containerName)) {
            // pid@nodename
            source = TraceOption.getOptionInt(options, TraceOption.PID) + "@" + TraceOption.getOption(options, TraceOption.NODENAME);
        } else {
            // just a container name.
            source = containerName;
        }
        recorder = new Recorder(source, tracer, String.format("http://%s:%d/api/v2", chost, cport));
    }

    public static final String SQL = TracerType.SQL.getName();
    public static final ConcurrentHashMap<String, BasicMain> HOLDER = new ConcurrentHashMap<>();
}
