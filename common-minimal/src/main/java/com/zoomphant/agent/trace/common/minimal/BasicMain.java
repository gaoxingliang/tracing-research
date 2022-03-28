package com.zoomphant.agent.trace.common.minimal;

import com.zoomphant.agent.trace.common.minimal.utils.*;

import java.io.*;
import java.lang.instrument.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class BasicMain {

    protected Recorder recorder;

    protected Map<String, String> options;

    protected String chost;
    protected int cport;
    protected String source;

    protected String jarFile;

    protected final Instrumentation inst;

    protected final ClassLoader whoLoadMe;

    protected final ScheduledExecutorService reporter;

    public BasicMain(String agentArgs, Instrumentation inst, ClassLoader whoLoadMe) {
        this.inst = inst;
        this.whoLoadMe = whoLoadMe;
        flushAgentArgs(agentArgs, true);
        TracerType tracerType = TracerType.valueOf(TraceOption.getOption(options, TraceOption.TRACER_TYPE));
        install();
        _start(tracerType, agentArgs, inst);
        reporter = Executors.newSingleThreadScheduledExecutor();
        reporter.scheduleAtFixedRate(() -> reportingContainerDiscoveryInfo(), 10, 30, TimeUnit.SECONDS);
    }



    public final void stop() {
        reporter.shutdown();
        if (recorder != null) {
            recorder.stop();
        }
        try {
            _stop();
        } catch (Exception e) {
            TraceLog.info("Fail to stop, " + e);
        }
    }

    /**
     * called when the instrumention is stopped. Do some cleanup stuff.
     */
    abstract protected void _stop();

    /**
     *
     * Use this to flush latest variables and make it visible for existing task
     * eg reporting Headers.
     *
     * @param agentArgs
     * @param firstTime  is this the first time??
     */
    public final void flushAgentArgs(String agentArgs, boolean firstTime) {
        options = TraceOption.parseOptions(agentArgs);
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
        if (firstTime) {
            this.jarFile = TraceOption.getOption(options, TraceOption.JARFILE);
        } else {
            // override
            _flushInternalVariables();
        }

    }

    /**
     * Reserved for subclass to flush internal variables when the agent args are changed.
     * Eg for jmx mains, it need to change the reporting headers too.
     */
    protected void _flushInternalVariables() {

    }

    public abstract void install();

    protected void _start(TracerType tracer, String agentArgs, Instrumentation inst) {
        recorder = new Recorder(source, tracer, String.format("http://%s:%d/trace/binary", chost, cport));
    }

    private void reportingContainerDiscoveryInfo() {
        try {
            ContainerDiscovery d = getContainerDiscovery();
            if (d != null) {
                try {
                    d.source = source;
                    d.getShareLabels().put(ContainerDiscovery.ProcessTypeLabel.pid, TraceOption.getOption(options, TraceOption.PID));
                    d.getShareLabels().put(ContainerDiscovery.ProcessTypeLabel.container_id, TraceOption.getOption(options, TraceOption.CONTAINER));
                    d.getShareLabels().put(ContainerDiscovery.ProcessTypeLabel.hostname, getHostName());
                    d.getShareLabels().put(ContainerDiscovery.ProcessTypeLabel.nodename, TraceOption.getOption(options, TraceOption.NODENAME));

                    HttpUtils.post(String.format("http://%s:%d/api/discover", chost, cport), OutputUtils.toBytes(d), null);
                }
                catch (IOException e) {
                    TraceLog.warn("Fail to post remote " + e.getMessage());
                }
            }
        } catch (Exception e) {
            TraceLog.warn("Fail to report discovery" + e);
        }
    }

    protected ContainerDiscovery getContainerDiscovery() {
        return new ContainerDiscovery();
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


    private static String getHostNameForLiunx() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (Exception uhe) {
            String host = uhe.getMessage(); // host = "hostname: hostname"
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }


    public static String getHostName() {
        if (System.getenv("COMPUTERNAME") != null) {
            return System.getenv("COMPUTERNAME") == null ? "UnknownHost" : System.getenv("COMPUTERNAME");
        } else {
            return getHostNameForLiunx();
        }
    }
}
