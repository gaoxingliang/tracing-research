package com.zoomphant.agent.trace.zookeeper;

import com.zoomphant.agent.trace.jmx.JMXBaseMain;

import java.lang.instrument.Instrumentation;

public class ZookeeperMain extends JMXBaseMain {


    public ZookeeperMain(String agentArgs, Instrumentation inst, ClassLoader cl) {
        super(agentArgs, inst, cl);
    }

    @Override
    public void install() {
        // do nothing
    }




    @Override
    protected String getConfigYaml() {
        return "zookeeperJmx.yaml";
    }
}

