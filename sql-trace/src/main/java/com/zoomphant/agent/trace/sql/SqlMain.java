package com.zoomphant.agent.trace.sql;

import com.zoomphant.agent.trace.common.BasicMain;
import com.zoomphant.agent.trace.common.MainHolders;
import com.zoomphant.agent.trace.common.TracerType;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.sql.Statement;

public class SqlMain extends BasicMain {

    public static void install(String agentArgs, Instrumentation inst, ClassLoader whoLoadedMe) {
        TraceLog.info("Will install sql with " + agentArgs);
        /**
         *
         * https://stackoverflow.com/questions/47571749/when-attaching-agent-to-running-process-bytebuddy-transformer-doesnt-seem-to-t
         *
         * The last part is required on most JVMs (with the notable exception of the dynamic code evolution VM, a custom build of HotSpot).
         * It tells Byte Buddy to not add fields or methods, what most VMs do not support.
         */
        SqlMain main = new SqlMain();
        if (!main.start(TracerType.SQL, agentArgs, inst)) {
            return;
        }

        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .with( //new AgentBuilder.Listener.WithErrorsOnly(
                        new AgentBuilder.Listener.WithTransformationsOnly(
                                AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .type(ElementMatchers.isSubTypeOf(Statement.class))
                .transform(new AgentBuilder.Transformer.ForAdvice()
                        .include(whoLoadedMe) // where to serach the execute class advice.
                        // use this to avoid the classes loading problem - https://stackoverflow
                        // .com/questions/60237664/classpath-problems-while-instrumenting-springboot-application
                        .advice(ElementMatchers.namedOneOf("executeQuery", "execute", "executeUpdate").and(ElementMatchers.isPublic()),
                                ExecuteAdvice.class.getName())).installOn(inst);
        MainHolders.register(SqlMain.class.getName(), main);
        TraceLog.info("Sql main installed using args " + agentArgs);

    }

}
