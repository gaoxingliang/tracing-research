package com.zoomphant.agent.trace.common;

import com.sun.tools.attach.VirtualMachine;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class VMUtil {
    // Attach a jvm with jar file and options
    public void attach(String pid, String jarFile, String option) throws Exception {
        if (!new File(jarFile).exists()) {
            throw new IllegalArgumentException("File not found - " + jarFile);
        }
        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = VirtualMachine.attach(pid);
            virtualMachine.loadAgent(jarFile, option);
            TraceLog.info("Loaded agent " + jarFile);
        } catch (Exception e) {
            TraceLog.error("Fail to load " + jarFile, e);
        } finally {
            if (virtualMachine != null) {
                virtualMachine.detach();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        VMUtil.attach("72712", "/Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar", "");
    }
}
