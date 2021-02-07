package com.zoomphant.agent.trace.common.rewrite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MemoryBatchReporter implements SpanReporter {

    private final String reportedTo;
    private ConcurrentSkipListSet<Span> spans;
    private BlockingQueue<Span> finished = new ArrayBlockingQueue<>(10240);

    public MemoryBatchReporter(String reportedTo) {
        this.reportedTo = reportedTo;
        this.spans = new ConcurrentSkipListSet();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> _report(), 10, 10, TimeUnit.SECONDS);
    }

    private void _report() {
        int max = 1024;
        List<Span> spans = new ArrayList<>(max);
        finished.drainTo(spans, max);
        if (!spans.isEmpty()) {
            // serialize all and report
            System.out.println("Found spans:" + spans.size());
        }
    }


    @Override
    public boolean start(Span s) {
        spans.add(s);
        return true;
    }

    @Override
    public boolean finish(Span s) {
        spans.remove(s);
        finished.add(s);
        return true;
    }

    @Override
    public void abandon(Span s) {
        spans.remove(s);
    }
}
