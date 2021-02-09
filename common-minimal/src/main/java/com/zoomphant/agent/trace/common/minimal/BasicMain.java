package com.zoomphant.agent.trace.common.minimal;

import com.zoomphant.agent.trace.common.minimal.utils.HttpUtils;
import com.zoomphant.agent.trace.common.minimal.utils.OutputUtils;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class BasicMain {

    protected Recorder recorder;

    protected Map<String, String> options;

    protected String chost;
    protected int cport;
    protected String source;

    protected String jarFile;

    protected final Instrumentation inst;

    protected final ClassLoader whoLoadMe;

    public BasicMain(String agentArgs, Instrumentation inst, ClassLoader whoLoadMe) {
        this.inst = inst;
        this.whoLoadMe = whoLoadMe;
        options = TraceOption.parseOptions(agentArgs);
        this.jarFile = TraceOption.getOption(options, TraceOption.JARFILE);
        chost = TraceOption.getOption(options, TraceOption.CENTRALHOST);
        cport = TraceOption.getOptionInt(options, TraceOption.CENTRALPORT);
        String containerName = TraceOption.getOption(options, TraceOption.CONTAINER);
        if (containerName == null || containerName.isEmpty()) {
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
        recorder = new Recorder(source, tracer, String.format("http://%s:%d/trace/binary", chost, cport));
    }

    private void reportingContainerDiscoveryInfo() {
        ContainerDiscovery d = getContainerDiscovery();
        if (d != null) {
            try {
                d.source = source;

                HttpUtils.post(String.format("http://%s:%d/api/discover", chost, cport), OutputUtils.toBytes(d), null);
            }
            catch (IOException e) {
                TraceLog.warn("Fail to post remote " + e.getMessage());
            }
        }
    }

    protected ContainerDiscovery getContainerDiscovery() {
        return null;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public String getChost() {
        return chost;
    }

    public int getCport() {
        return cport;
    }

    public String getSource() {
        return source;
    }

    public String getJarFile() {
        return jarFile;
    }

    public Instrumentation getInst() {
        return inst;
    }

    public ClassLoader getWhoLoadMe() {
        return whoLoadMe;
    }


}
