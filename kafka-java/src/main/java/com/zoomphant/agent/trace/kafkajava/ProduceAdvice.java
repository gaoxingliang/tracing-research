package com.zoomphant.agent.trace.kafkajava;

import com.zoomphant.agent.trace.common.minimal.Custom;
import com.zoomphant.agent.trace.common.minimal.MainHolders;
import com.zoomphant.agent.trace.common.minimal.Span;
import net.bytebuddy.asm.Advice;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;

/**
 * intercept some jvm using kafka producer
 */
public class ProduceAdvice {
    public static final String NAME = "com.zoomphant.agent.trace.kafkajava.KafkaJavaMain";

    @Advice.OnMethodEnter(suppress = Throwable.class)
    static Span enter(@Advice.Argument(0) ProducerRecord record,
                      @Custom ProducerConfig producerConfig) {
        // target remote url:
        try {
            List<String> target = producerConfig.getList("bootstrap.servers");
            return MainHolders.get(NAME).getRecorder().recordStart("send", target.toString(), "__datatype", "kafka", "kafka.topic", record.topic());
        }
        catch (Throwable throwables) {
            return null;
        }

    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable th) {
        if (span != null) {
            MainHolders.get(NAME).getRecorder().recordFinish(span, th);
        }
    }
}
