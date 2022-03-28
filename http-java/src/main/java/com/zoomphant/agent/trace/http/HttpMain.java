package com.zoomphant.agent.trace.http;

import com.zoomphant.agent.trace.common.minimal.*;
import net.bytebuddy.agent.builder.*;
import net.bytebuddy.matcher.*;

import java.lang.instrument.*;
import java.sql.*;

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
        resettableClassFileTransformer = new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .with( //new AgentBuilder.Listener.WithErrorsOnly(
                        new AgentBuilder.Listener.WithTransformationsOnly(
                                AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .type(ElementMatchers.isSubTypeOf(Statement.class))
                .transform(new AgentBuilder.Transformer.ForAdvice()
                        .include(whoLoadMe) // where to search the advice.
                        // use this to avoid the classes loading problem - https://stackoverflow
                        // .com/questions/60237664/classpath-problems-while-instrumenting-springboot-application
                        .advice(ElementMatchers.namedOneOf("executeQuery", "execute", "executeUpdate").and(ElementMatchers.isPublic()),
                                GoogleHttpAdvice.class.getName())).installOn(inst);
        TraceLog.info("Google http main installed");
    }
}
