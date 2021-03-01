package com.zoomphant.agent.trace.sql;

import com.zoomphant.agent.trace.common.minimal.BasicMain;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.sql.Statement;

public class SqlMain extends BasicMain {


    public SqlMain(String agentArgs, Instrumentation inst, ClassLoader classLoader) {
        super(agentArgs, inst, classLoader);
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
                                ExecuteAdvice.class.getName())).installOn(inst);
        TraceLog.info("Sql main installed");
    }


    public static void premain(String agentArgs, Instrumentation inst) {
        new SqlMain(agentArgs, inst, null);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        new SqlMain(agentArgs, inst, null);
    }

}
