package com.zoomphant.agent.trace.common;

import com.alibaba.fastjson.JSONObject;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.minimal.TraceOption;
import com.zoomphant.agent.trace.common.minimal.TracerType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Map;
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
    protected String jarFile;

    @Getter
    protected final Instrumentation inst;

    @Getter
    protected final ClassLoader whoLoadMe;
    public BasicMain(String agentArgs, Instrumentation inst, ClassLoader whoLoadMe) {
        this.inst = inst;
        this.whoLoadMe = whoLoadMe;
        options = TraceOption.parseOptions(agentArgs);
        this.jarFile = TraceOption.getOption(options, TraceOption.JARFILE);
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
        TracerType tracerType = TracerType.valueOf(TraceOption.getOption(options, TraceOption.TRACER_TYPE));
        _start(tracerType, agentArgs, inst);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> reportingContainerDiscoveryInfo(), 10, 30, TimeUnit.SECONDS);
        install();
    }

    public abstract void install();

    protected void _start(TracerType tracer, String agentArgs, Instrumentation inst) {
        recorder = new Recorder(source, tracer, String.format("http://%s:%d/api/v2", chost, cport));
    }

    private void reportingContainerDiscoveryInfo() {
        ContainerDiscovery d = getContainerDiscovery();
        if (d != null) {
            try {
                d.source = source;

                HttpUtils.post(String.format("http://%s:%d/api/discover", chost, cport), JSONObject.toJSONString(d), null);
            }
            catch (IOException e) {
                TraceLog.warn("Fail to post remote " + e.getMessage());
            }
        }
    }

    protected ContainerDiscovery getContainerDiscovery() {
        return null;
    }

}
