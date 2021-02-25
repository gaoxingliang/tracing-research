package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.minimal.TracerType;

public class ApacheHttpClientChecker extends JavaChecker {
    @Override
    public TracerType supportedTracers() {
        return TracerType.APACHE_HTTPCLIENT_JAVA;
    }
}
