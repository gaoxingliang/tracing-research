package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.Tracer;

public class SQLChecker extends JavaChecker{
    @Override
    public Tracer supportedTracers() {
        return Tracer.SQL;
    }
}
