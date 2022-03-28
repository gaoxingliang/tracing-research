package com.zoomphant.agent.trace.kafkajava;

import com.zoomphant.agent.trace.common.minimal.*;
import net.bytebuddy.agent.builder.*;
import net.bytebuddy.asm.*;
import net.bytebuddy.matcher.*;
import org.apache.kafka.clients.producer.*;

import java.lang.instrument.*;

public class KafkaJavaMain extends BasicMain {

    private ResettableClassFileTransformer resettableClassFileTransformer;

    @Override
    protected void _stop() {
        if (resettableClassFileTransformer != null) {
            resettableClassFileTransformer.reset(inst, AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
        }
    }

    public KafkaJavaMain(String agentArgs, Instrumentation inst, ClassLoader whoLoadMe) {
        super(agentArgs, inst, whoLoadMe);
    }

    @Override
    public void install() {
        try {
            resettableClassFileTransformer = new AgentBuilder.Default()
                    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                    .disableClassFormatChanges()
                    .with( //new AgentBuilder.Listener.WithErrorsOnly(
                            new AgentBuilder.Listener.WithTransformationsOnly(
                                    AgentBuilder.Listener.StreamWriting.toSystemOut()))
                    .type(ElementMatchers.isSubTypeOf(KafkaProducer.class))
                    .transform(
                            new AgentBuilder.Transformer.ForAdvice(
                                    Advice.withCustomMapping().bind(Custom.class,
                                            KafkaProducer.class.getDeclaredField("producerConfig")))
                                    .include(whoLoadMe) // where to search the advice.
                                    .advice(ElementMatchers.named("send").and(ElementMatchers.isPublic()),
                                            ProduceAdvice.class.getName())).installOn(inst);
            TraceLog.info("Kafka java main installed");
        }
        catch (Exception e) {
            TraceLog.error("Fail to install kafka java", e);
        }
    }
}
