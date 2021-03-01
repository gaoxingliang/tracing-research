package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.minimal.TracerType;

public class SQLChecker extends JavaChecker{
    @Override
    public TracerType supportedTracers() {
        return TracerType.SQL_JAVA;
    }
}
