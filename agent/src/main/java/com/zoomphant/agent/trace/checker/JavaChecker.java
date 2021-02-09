package com.zoomphant.agent.trace.checker;

public abstract class JavaChecker extends Checker {
    @Override
    public DiscoveredInfo _check(ProcInfo procInfo) {
        if (procInfo.cmd.contains("java")) {
            return DiscoveredInfo.newJava();
        }
        return null;
    }
}
