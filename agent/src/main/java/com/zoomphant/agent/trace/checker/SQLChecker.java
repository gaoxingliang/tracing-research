package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.TracerType;

public class SQLChecker extends JavaChecker{
    @Override
    public TracerType supportedTracers() {
        return TracerType.SQL;
    }
}
