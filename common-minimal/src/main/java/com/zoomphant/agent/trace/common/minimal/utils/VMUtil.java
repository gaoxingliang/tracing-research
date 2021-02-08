package com.zoomphant.agent.trace.common.minimal.utils;

import com.sun.tools.attach.VirtualMachine;
import com.zoomphant.agent.trace.common.minimal.TraceLog;

public class VMUtil {
    // Attach a jvm with jar file and options
    public static void attach(String pid, String jarFile, String option) throws Exception {

        VirtualMachine virtualMachine = null;
        try {
            TraceLog.info("Try loading agent " + jarFile + " for pid = " + pid + " with option=" + option);

            //ByteBuddyAgent.attach(new File(jarFile), pid, option);
            virtualMachine = VirtualMachine.attach(pid);
            virtualMachine.loadAgent(jarFile, option);
            TraceLog.info("Loaded agent " + jarFile + " for pid = " + pid + " with option=" + option);
        }
        catch (Exception e) {
            if (ExceptionUtils.rootCause(e).toString().contains("AgentLoadException: 0")) {
                // when attach on a jdk8 process using jdk11...
                // it's normal here.
            }
            else {
                TraceLog.error("Fail to load " + jarFile, e);
            }
        }
        finally {
            if (virtualMachine != null) {
                virtualMachine.detach();
            }
        }
    }

}
