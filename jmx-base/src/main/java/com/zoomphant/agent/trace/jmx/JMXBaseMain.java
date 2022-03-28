package com.zoomphant.agent.trace.jmx;

import com.zoomphant.agent.trace.common.minimal.*;
import com.zoomphant.agent.trace.common.minimal.utils.*;
import io.prometheus.jmx.shaded.io.prometheus.client.*;
import io.prometheus.jmx.shaded.io.prometheus.client.exporter.common.*;
import io.prometheus.jmx.shaded.io.prometheus.client.hotspot.*;
import io.prometheus.jmx.shaded.io.prometheus.jmx.*;

import java.io.*;
import java.lang.instrument.*;
import java.util.*;
import java.util.concurrent.*;

public class JMXBaseMain extends BasicMain {
    protected String prometheusReportedTo;

    /**
     * the headers all starts with the prefix in {@link TraceOption#REPORTING_HEADER_PREFIX}
     **/
    protected Map<String, String> reportingHeaders;

    @Override
    protected void _stop() {
    }

    public JMXBaseMain(String agentArgs, Instrumentation inst, ClassLoader cl) {
        super(agentArgs, inst, cl);
    }

    @Override
    public void install() {

    }

    /**
     * get the jmx exporter yaml config file path
     * @return
     */
    protected String getConfigYaml() {
        return "defaultJmxConfig.yaml";
    }

    /**
     * if you want to fill in additional headers.. override this
     * @return the additional headers SHOULD NOT starts with the prefix in {@link TraceOption#REPORTING_HEADER_PREFIX}
     *  JUST THE original headers.
     */
    protected Map<String, String> getOtherReportingHeaders() {
        return new HashMap<>(0);
    }

    @Override
    protected void _start(TracerType tracer, String agentArgs, Instrumentation inst) {
        _flushInternalVariables();
        TraceLog.info("Reporting headers" + reportingHeaders);

        // build the collector
        String filePath = getConfigYaml();
        String fileContent = null;
        try {
            fileContent = FileUtils.getFile(JMXBaseMain.class.getClassLoader(), filePath);
            CollectorRegistry registry = new CollectorRegistry(true);
            final JmxCollector jmxCollector = new JmxCollector(fileContent);
            jmxCollector.register(registry);
            DefaultExports.register(registry);
            reporter.scheduleAtFixedRate(() -> collect(registry), 10, 60, TimeUnit.SECONDS);
            TraceLog.info("Loaded jmx agent " + filePath);
        }
        catch (Throwable e) {
            TraceLog.warn("Fail to load " + ExceptionUtils.fullStack(e));
        }
    }

    @Override
    protected void _flushInternalVariables() {
        // parse the options...
        prometheusReportedTo = String.format("http://%s:%d/prometheus", chost, cport);
        reportingHeaders = TraceOption.filterReportingHeaders(options);

        Map<String, String> other = getOtherReportingHeaders();
        if (!other.isEmpty()) {
            reportingHeaders.putAll(TraceOption.buildReportingHeaders(other));
        }
    }

    private void collect(CollectorRegistry registry) {
        try {
            //TraceLog.info("start collecting jmx data");
            StringWriter sw = new StringWriter(1024 * 4);
            TextFormat.write004(sw, registry.metricFamilySamples());
            HttpUtils.post(prometheusReportedTo, sw.toString(), reportingHeaders);
            //TraceLog.info("Posting data " + prometheusReportedTo + " data " + StringUtils.abbr(sw.toString(), 100));
        } catch (Throwable e) {
            TraceLog.info("Fail to report " + ExceptionUtils.fullStack(e));
        }
    }
}
