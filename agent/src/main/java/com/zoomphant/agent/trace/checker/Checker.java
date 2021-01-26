package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.TraceLog;
import com.zoomphant.agent.trace.common.Tracer;

import java.util.HashMap;
import java.util.Map;

public abstract class Checker {

    public final DiscoveredInfo check(ProcInfo procInfo) {
        try {
            return _check(procInfo);
        } catch (Throwable e) {
            TraceLog.error("Fail to parse " + procInfo, e);
        }
        return null;
    }

    public final Map<String, String> options() {
        Map<String, String> options = _options();
        if (options == null) {
            return new HashMap<>(0);
        }
        return options;
    }

    public abstract Tracer supportedTracers();

    protected abstract Map<String, String> _options();

    protected abstract DiscoveredInfo _check(ProcInfo procInfo);
}
