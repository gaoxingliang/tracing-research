package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.minimal.TracerType;

import java.util.regex.Pattern;

public class GenericJVMChecker extends JavaChecker {

    private final String cmdRegex;

    /**
     * The cmd regex
     * @param cmdRegex
     */
    public GenericJVMChecker(String cmdRegex) {
        if (cmdRegex == null || cmdRegex.isEmpty()) {
            cmdRegex = ".*";
        }
        this.cmdRegex = cmdRegex;
    }


    @Override
    public DiscoveredInfo _check(ProcInfo procInfo) {
        DiscoveredInfo discoveredInfo = super._check(procInfo);
        if (discoveredInfo != null) {
            // this must be a java process
            try {
                if (Pattern.matches(cmdRegex, procInfo.getCmd())) {
                    return discoveredInfo;
                }
            } catch (Exception e){}
        }
        return null;
    }

    @Override
    public TracerType supportedTracers() {
        return TracerType.JMX_BASE;
    }
}
