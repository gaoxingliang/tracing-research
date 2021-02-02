package com.zoomphant.agent.trace.common;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasicMain {

    @Getter
    protected Recorder recorder;
    @Getter
    protected Map<String, String> options;

    protected String chost;
    protected int cport;
    protected String source;

    protected void _start(TracerType tracer, String agentArgs, Instrumentation inst) {
        recorder = new Recorder(source, tracer, String.format("http://%s:%d/api/v2", chost, cport));
    }

    public final void start(TracerType tracer, String agentArgs, Instrumentation inst) {
        options = TraceOption.parseOptions(agentArgs);
        chost = TraceOption.getOption(options, TraceOption.CENTRALHOST);
        cport = TraceOption.getOptionInt(options, TraceOption.CENTRALPORT);
        String containerName = TraceOption.getOption(options, TraceOption.CONTAINER);
        if (StringUtils.isEmpty(containerName)) {
            // pid@nodename
            source = TraceOption.getOptionInt(options, TraceOption.PID) + "@" + TraceOption.getOption(options, TraceOption.NODENAME);
        } else {
            // just a container name.
            source = containerName;
        }
        _start(tracer, agentArgs, inst);
    }

    public static final ConcurrentHashMap<TracerType, BasicMain> HOLDER = new ConcurrentHashMap<>();
}
