package com.zoomphant.agent.trace.common.minimal;



import com.zoomphant.agent.trace.common.minimal.utils.HttpUtils;
import com.zoomphant.agent.trace.common.minimal.utils.OutputUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MemoryBatchReporter implements SpanReporter {

    private final String reportedTo;
    private WeakConcurrentMap<String, Span> spans;
    private BlockingQueue<Span> finished = new ArrayBlockingQueue<>(10240);

    public MemoryBatchReporter(String reportedTo) {
        this.reportedTo = reportedTo;
        this.spans = new WeakConcurrentMap<>();
        Executors.newSingleThreadExecutor().submit(() -> _report());
    }

    private void _report() {
        try {
            int max = 10;
            List<Span> spans = new ArrayList<>(max);
            long lastReported = 0;
            long reportInterval = TimeUnit.SECONDS.toMillis(5);
            Span span = null;
            while (true) {
                try {
                     span = finished.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    break;
                }
                if (span != null) {
                    spans.add(span);
                }
                if (spans.size() >= max || System.currentTimeMillis() - lastReported > reportInterval) {
                    // serialize all and report
                    // encoding all spans.
                    ListOfSpans s = new ListOfSpans();
                    s.setSpanList(spans);
                    byte[] bytes = OutputUtils.toBytes(s);
                    try {
                        HttpUtils.post(reportedTo, bytes, null);
                    } catch (Exception e) {
                        TraceLog.warn("Fail to report" + e.getMessage());
                    }
                    spans.clear();
                    lastReported = System.currentTimeMillis();
                }
            }
        }
        catch (Throwable e) {
            TraceLog.warn("Fail to report " + e);
        }
    }


    @Override
    public boolean start(Span s) {
        spans.putIfProbablyAbsent(s.getId(), s);
        return true;
    }

    @Override
    public boolean finish(Span s) {
        finished.add(s);
        spans.remove(s.getId());
        return true;
    }

    @Override
    public void abandon(Span s) {
        spans.remove(s.getId());
    }
}
