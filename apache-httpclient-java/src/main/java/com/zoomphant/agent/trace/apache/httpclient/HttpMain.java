package com.zoomphant.agent.trace.apache.httpclient;

import com.zoomphant.agent.trace.common.minimal.BasicMain;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import net.bytebuddy.agent.builder.*;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import java.lang.instrument.Instrumentation;

public class HttpMain extends BasicMain {

    private ResettableClassFileTransformer resettableClassFileTransformer;

    @Override
    protected void _stop() {
        if (resettableClassFileTransformer != null) {
            resettableClassFileTransformer.reset(inst, AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
        }
    }

    public HttpMain(String agentArgs, Instrumentation inst, ClassLoader whoLoadMe) {
        super(agentArgs, inst, whoLoadMe);
    }



    @Override
    public void install() {
        /**
         *     HttpResponse execute(HttpHost target, HttpRequest request,
         *                          HttpContext context)
         */
        resettableClassFileTransformer = new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .with( //new AgentBuilder.Listener.WithErrorsOnly(
                        new AgentBuilder.Listener.WithTransformationsOnly(
                                AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .type(ElementMatchers.isSubTypeOf(HttpClient.class)
                        .and(ElementMatchers.not(ElementMatchers.isSubTypeOf(CloseableHttpClient.class))))
                .transform(advice4HttpClient())
                .type(ElementMatchers.isSubTypeOf(CloseableHttpClient.class)).transform(advice4ClosableHttpClient())
                .installOn(inst);
        TraceLog.info("Apache http main installed");
    }


    private AgentBuilder.Transformer.ForAdvice advice4HttpClient() {
        return new AgentBuilder.Transformer.ForAdvice()
                .include(whoLoadMe)
                .advice(ElementMatchers.named("execute")
                                .and(ElementMatchers.isPublic())
                                .and(ElementMatchers.takesArguments(HttpHost.class, HttpRequest.class, HttpContext.class) ),
                        ApacheHttpAdvice.class.getName());
    }


    private AgentBuilder.Transformer.ForAdvice advice4ClosableHttpClient() {
        return new AgentBuilder.Transformer.ForAdvice()
                .include(whoLoadMe)
                .advice(ElementMatchers.named("doExecute")
                                .and(ElementMatchers.isProtected())
                                .and(ElementMatchers.takesArguments(HttpHost.class, HttpRequest.class, HttpContext.class) ),
                        ApacheHttpAdvice.class.getName());
    }

}
