package com.zoomphant.agent.trace.checker;

import java.util.Map;

public abstract class JavaChecker extends Checker {
    @Override
    protected Map<String, String> _options() {
        return null;
    }

    @Override
    public DiscoveredInfo _check(ProcInfo procInfo) {
        if (procInfo.cmd.contains("java")) {
            return DiscoveredInfo.newJava();
        }
        return null;
    }
}
