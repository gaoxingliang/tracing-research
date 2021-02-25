package com.zoomphant.agent.trace.apache.httpclient;

import com.zoomphant.agent.trace.common.minimal.MainHolders;
import com.zoomphant.agent.trace.common.minimal.Span;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import net.bytebuddy.asm.Advice;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public class ApacheHttpAdvice {
    public static final String NAME = "com.zoomphant.agent.trace.apache.httpclient.HttpMain";

    @Advice.OnMethodEnter(suppress = Throwable.class)
    static Span enter(@Advice.Argument(0) HttpHost target,
                      @Advice.Argument(1) HttpRequest request,
                      @Advice.Argument(2) HttpContext context) {
        try {
            TraceLog.info("Got trace " + target);
            return MainHolders.get(NAME).getRecorder().recordStart("send", target.getHostName());
        }
        catch (Throwable throwables) {
            TraceLog.error("Fail to send", throwables);
            return null;
        }
    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable th,
                     @Advice.Return HttpResponse response) {
        if (span != null) {
            try {
                boolean suc = th == null && response.getStatusLine().getStatusCode() == 200;
                MainHolders.get(NAME).getRecorder().recordFinish(span, suc);
            } catch (Throwable e) {
                TraceLog.error("Fail to record finish", e);
            }
        }
    }
}
