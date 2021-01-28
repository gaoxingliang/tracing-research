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

    /**
     *
     * @param op the operation
     * @param target  the remote target
     * @param tags  additional tags if needed
     * @return the span
     */
    public Span recordStart(String op, String target, String... tags) {
        Span s = tracer.newTrace();
        s.name(op).remoteServiceName(tracerType.getName() + "@" + target);
        if (tags != null) {
            for (int i = 0; i < tags.length / 2; ) {
                s.tag(tags[i], tags[i + 1]);
                i = i + 2;
            }
        }
        s.tag("_state", "");
        s.start();
        TraceLog.info("Started span " + s );
        return s;
    }

    public void recordFinish(Span span, Throwable e) {
        span.tag("_state", e == null ? "suc" : "fail");
        if (e != null) {
            span.error(e);
        }
        span.finish();
        TraceLog.info("Finished span " + span);
    }
}
