package com.zoomphant.agent.trace.kafkajava;

import com.zoomphant.agent.trace.common.minimal.BasicMain;
import com.zoomphant.agent.trace.common.minimal.Custom;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.lang.instrument.Instrumentation;

public class KafkaJavaMain extends BasicMain {

    public KafkaJavaMain(String agentArgs, Instrumentation inst, ClassLoader whoLoadMe) {
        super(agentArgs, inst, whoLoadMe);
    }

    @Override
    public void install() {
        try {
            new AgentBuilder.Default()
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
