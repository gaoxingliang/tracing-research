package com.zoomphant.agent.trace.common;

import io.prometheus.jmx.shaded.io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.jmx.shaded.io.prometheus.jmx.JmxCollector;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.StringWriter;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JMXMain extends BasicMain {
    protected String prometheusReportedTo;
    protected String serviceName;
    protected Map<String, String> reportingHeaders;


    protected String getConfigYaml() {
        return "defaultJmxConfig.yaml";
    }

    @Override
    protected void _start(TracerType tracer, String agentArgs, Instrumentation inst) {
        // parse the options...
        prometheusReportedTo = String.format("http://%s:%d/prometheus", chost, cport);
        serviceName = tracer.getName() + "@" + source;
        reportingHeaders = TraceOption.extractReportingHeaders(options);
        TraceLog.info("Reporting headers" + reportingHeaders);
        // build the collector
        String filePath = getConfigYaml();
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
