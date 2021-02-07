package com.zoomphant.agent.trace.common;

import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.minimal.TracerType;
import com.zoomphant.agent.trace.common.rewrite.MemoryBatchReporter;
import com.zoomphant.agent.trace.common.rewrite.Span;
import com.zoomphant.agent.trace.common.rewrite.SpanReporter;
import com.zoomphant.agent.trace.common.rewrite.Tracer;
import lombok.Getter;
public class Recorder {

    @Getter
    private String source;
    private final Tracer tracer;
    private final TracerType tracerType;

    public Recorder(String source, TracerType tracerType, String reportedToUrl) {
        this.source = source;
        this.tracerType = tracerType;
        // Configure a reporter, which controls how often spans are sent
        SpanReporter s = new MemoryBatchReporter(reportedToUrl);
        this.tracer = new Tracer(source, s);
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
        s.setName(op);
        s.setRemote(tracerType.getName() + "@" + target);
        if (tags != null) {
            for (int i = 0; i < tags.length / 2; ) {
                s.tag(tags[i], tags[i + 1]);
                i = i + 2;
            }
        }
        s.tag("_state", "");
        s.start();
        TraceLog.debug("Started span " + s);
        return s;
    }

    public void recordFinish(Span span, Throwable e) {
        span.tag("_state", e == null ? "suc" : "fail");
        if (e != null) {
            span.error();
        }
        span.finish();
        TraceLog.debug("Finish span " + span);
    }
}
