package com.zoomphant.agent.trace;

import java.lang.instrument.Instrumentation;

public class DummyAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("DummyAgent in premain " + agentArgs);
        listAll(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("DummyAgent in agentmain " + agentArgs);
        listAll(inst);
    }

    private static void listAll(Instrumentation instrumentation) {
//        for (Class c : instrumentation.getAllLoadedClasses()) {
//            System.out.println(c.getCanonicalName() + " loaded by" + c.getClassLoader());
//        }
//        System.out.println("Total " + instrumentation.getAllLoadedClasses().length);
//

        try {
            Class c = Class.forName("org.apache.kafka.clients.producer.KafkaProducer");
            System.out.println("loaded by " + c.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
