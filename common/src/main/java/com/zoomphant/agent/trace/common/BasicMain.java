package com.zoomphant.agent.trace.common;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class BasicMain {

    @Getter
    protected Recorder recorder;
    @Getter
    protected Map<String, String> options;

    protected String chost;
    protected int cport;
    protected String source;

    @Getter
    protected String agentArgs;

    protected void _start(TracerType tracer, String agentArgs, Instrumentation inst) {
        recorder = new Recorder(source, tracer, String.format("http://%s:%d/api/v2", chost, cport));
    }

    public final boolean start(TracerType tracer, String agentArgs, Instrumentation inst) {

        /**
         * let's check whether we have enabled this if so, let's do not do it anymore.
         */
        BasicMain existed = HOLDER.get(tracer);
        if (existed != null) {
            TraceLog.info("The tracer already exists. do not start this time anymore. " + tracer + " " + existed.agentArgs);
            return false;
        }

        this.agentArgs = agentArgs;

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
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> reportingContainerDiscoveryInfo(), 10, 30, TimeUnit.SECONDS);
        return true;
    }

    private void reportingContainerDiscoveryInfo() {
        ContainerDiscovery d = getContainerDiscovery();
        if (d != null) {
            try {
                d.source = source;
                HttpUtils.post(String.format("http://%s:%d/api/discover", chost, cport), JSONObject.toJSONString(d), new HashMap<>(0));
            }
            catch (IOException e) {
                TraceLog.warn("Fail to post remote " + e.getMessage());
            }
        }
    }

    protected ContainerDiscovery getContainerDiscovery() {
        return null;
    }




    public static final ConcurrentHashMap<TracerType, BasicMain> HOLDER = new ConcurrentHashMap<>();
}
