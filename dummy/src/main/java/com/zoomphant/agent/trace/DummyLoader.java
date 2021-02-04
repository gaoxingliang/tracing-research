package com.zoomphant.agent.trace;

import com.sun.tools.attach.VirtualMachine;

import java.util.Arrays;

public class DummyLoader {
    /**
     * agrs [0] --> pid
     * args [1] --> jar
     * args [2] --> possiable args
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Try attaching use :" + Arrays.toString(args));
        VirtualMachine virtualMachine = null;
        virtualMachine = VirtualMachine.attach(args[0]);
        virtualMachine.loadAgent(args[1], args.length == 2 ? "" : args[2]);
        System.out.println("Agent loaded");
        virtualMachine.detach();
    }
}
