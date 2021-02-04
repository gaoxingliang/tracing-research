package com.zoomphant.agent.trace;

import java.lang.instrument.Instrumentation;

public class DummyAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("DummyAgent in premain " + agentArgs);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("DummyAgent in agentmain " + agentArgs);
    }

}
