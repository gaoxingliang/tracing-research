package com.zoomphant.agent.trace.common;

import com.sun.tools.attach.VirtualMachine;
import lombok.experimental.UtilityClass;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
            ByteBuddyAgent.attach(new File(jarFile), pid, option);
            //virtualMachine = VirtualMachine.attach(pid);
            //virtualMachine.loadAgent(jarFile, option);
            TraceLog.info("Loaded agent " + jarFile + " for pid = " + pid);
        }
        catch (Exception e) {
            if (ExceptionUtils.getRootCauseMessage(e).contains("AgentLoadException: 0")) {
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
