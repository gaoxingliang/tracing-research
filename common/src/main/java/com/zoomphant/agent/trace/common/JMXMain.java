package com.zoomphant.agent.trace.common;

import io.prometheus.jmx.shaded.io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.jmx.shaded.io.prometheus.jmx.JmxCollector;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.StringWriter;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JMXMain extends BasicMain {
    protected String prometheusReportedTo;
    protected String serviceName;

    /**
     * the headers all starts with the prefix in {@link TraceOption#REPORTING_HEADER_PREFIX}
     **/
    protected Map<String, String> reportingHeaders;

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
        // parse the options...
        prometheusReportedTo = String.format("http://%s:%d/prometheus", chost, cport);
        serviceName = tracer.getName() + "@" + source;
        reportingHeaders = TraceOption.filterReportingHeaders(options);
        // build the collector
        String filePath = getConfigYaml();
        Map<String, String> other = getOtherReportingHeaders();
        if (!other.isEmpty()) {
            reportingHeaders.putAll(TraceOption.buildReportingHeaders(other));
        }
        TraceLog.info("Reporting headers" + reportingHeaders);

        String fileContent = null;
        try {
            fileContent = FileUtils.getFile(filePath);
            final JmxCollector jmxCollector = new JmxCollector(fileContent);
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> collect(jmxCollector), 10, 60, TimeUnit.SECONDS);
            TraceLog.info("Loaded agent " + filePath);
        }
        catch (Exception e) {
            TraceLog.error("Fail to load", e);
        }
    }

    private void collect(JmxCollector jmxCollector) {
        try {
            StringWriter sw = new StringWriter(1024 * 4);
            TextFormat.write004(sw, Collections.enumeration(jmxCollector.collect()));
            HttpUtils.post(prometheusReportedTo, sw.toString(), reportingHeaders);
            TraceLog.info("Posting data " + sw.toString());
        } catch (Exception e) {
            TraceLog.info("Fail to report " + ExceptionUtils.getRootCauseMessage(e));
        }
    }
}
