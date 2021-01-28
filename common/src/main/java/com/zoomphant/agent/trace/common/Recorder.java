package com.zoomphant.agent.trace.common;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import lombok.Getter;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class Recorder {

    @Getter
    private String source;
    private final Tracer tracer;
    private final TracerType tracerType;

    public Recorder(String source, TracerType tracerType, String reportedToUrl) {
        this.source = source;
        this.tracerType = tracerType;

        // Configure a reporter, which controls how often spans are sent
//   (this dependency is io.zipkin.reporter2:zipkin-sender-okhttp3)
        // http://127.0.0.1:9411/api/v2/spans
        OkHttpSender sender = OkHttpSender.create(reportedToUrl);
//   (this dependency is io.zipkin.reporter2:zipkin-reporter-brave)
        AsyncZipkinSpanHandler zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender);

// Create a tracing component with the service name you want to see in Zipkin.
        Tracing tracing = Tracing.newBuilder()
                .localServiceName(source)
                .addSpanHandler(zipkinSpanHandler)
                .build();
        tracer = tracing.tracer();
    }

    public Span recordStart(String op, String target, String... tags) {
        Span s = tracer.newTrace();
        s.name(op);
        if (tags != null) {
            for (int i = 0; i < tags.length / 2; ) {
                s.tag(tags[i], tags[i + 1]);
                i = i + 2;
            }
        }
        s.tag("_source", source);
        s.tag("_target", target);
        s.tag("_state", "");
        s.tag("_type", tracerType.getName());
        s.start();
        TraceLog.info("Started span " + s );
        return s;
    }

    public void recordFinish(Span span, boolean suc) {
        span.tag("_state", suc ? "suc" : "fail");
        span.finish();
        TraceLog.info("Finished span " + span);
    }
}
