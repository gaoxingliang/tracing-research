package com.zoomphant.agent.trace.checker;

import com.zoomphant.agent.trace.common.minimal.TracerType;

/**
 * check Zookeeper jvm processes
 */
public class ZookeeperChecker extends JavaChecker{

    @Override
    public DiscoveredInfo _check(ProcInfo procInfo) {
        if (procInfo.getCmd().contains("QuorumPeerMain")) {
            return DiscoveredInfo.newZk();
        }
        return null;
    }

    @Override
    public TracerType supportedTracers() {
        return TracerType.ZOOKEEPER_TRACE;
    }
}
