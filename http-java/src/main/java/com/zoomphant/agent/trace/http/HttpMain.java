package com.zoomphant.agent.trace.http;

import com.zoomphant.agent.trace.common.minimal.BasicMain;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.sql.Statement;

public class HttpMain extends BasicMain {

    public HttpMain(String agentArgs, Instrumentation inst, ClassLoader whoLoadMe) {
        super(agentArgs, inst, whoLoadMe);
    }

    @Override
    public void install() {
        new AgentBuilder.Default()
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
