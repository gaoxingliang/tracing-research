package com.zoomphant.agent.trace.kafka;

import com.zoomphant.agent.trace.common.minimal.ContainerDiscovery;
import com.zoomphant.agent.trace.jmx.JMXBaseMain;
import com.zoomphant.agent.trace.jmx.JmxUtils;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KafkaMain extends JMXBaseMain {

    private String clusterId;
    private String brokerId;
    private String zkurl;

    public KafkaMain(String agentArgs, Instrumentation inst, ClassLoader cl) {
        super(agentArgs, inst, cl);
    }

    @Override
    public void install() {
        // do nothing
    }

    @Override
    protected String getConfigYaml() {
        String name = "kafka.server:type=app-info";
        String version = JmxUtils.getValue(name, "version");
        return version.startsWith("2.") ? "kafka2_0_JMX.yaml" :"kafkaJmx.yaml";
    }

    @Override
    protected Map<String, String> getOtherReportingHeaders() {
        /**
         * for kafka add the cluster id and broker Id
         */
        Map<String, String> map = new HashMap<>(2);
        List<String> ids = JmxUtils.getNodes("kafka.server:id=*,type=app-info", "id");
        if (!ids.isEmpty()) {
            brokerId = ids.get(0);
            map.put(ContainerDiscovery.ProcessTypeLabel.kafka_brokerid + "", ids.get(0));
        }
        String clusterId = JmxUtils.getValue("kafka.server:type=KafkaServer,name=ClusterId", "Value");
        map.put(ContainerDiscovery.ProcessTypeLabel.kafka_clusterid + "", clusterId);
        this.clusterId = clusterId;

        /**
         * hacky way to list out all threads named with
         * "main-SendThread(localhost:12181)"
         * This is the kafka used to connect with the zookeeper
         */
        Optional<Thread> op = Thread.getAllStackTraces().keySet().stream().filter(th -> th.getName().startsWith("main-SendThread(")).findAny();
        if (op.isPresent()) {
            String threadname = op.get().getName();
            String zkurl = threadname.substring(threadname.indexOf("(") + 1, threadname.length() - 1);
            this.zkurl = zkurl;
        }

        return map;
    }

    @Override
    protected ContainerDiscovery getContainerDiscovery() {
        Map<ContainerDiscovery.ProcessTypeLabel, String> adMap = new HashMap<>(2);
        if (clusterId != null) {
            adMap.put(ContainerDiscovery.ProcessTypeLabel.kafka_clusterid, clusterId);
        }
        if (brokerId != null) {
            adMap.put(ContainerDiscovery.ProcessTypeLabel.kafka_brokerid, brokerId);
        }
        if (zkurl != null) {
            adMap.put(ContainerDiscovery.ProcessTypeLabel.kafka_zkurl, zkurl);
        }

        if (adMap.isEmpty()) {
            return null;
        }
        ContainerDiscovery containerDiscovery = new ContainerDiscovery();
        containerDiscovery.getProcessTypeMap().put(ContainerDiscovery.ProcessType.KAFKA, adMap);
        return containerDiscovery;
    }
}

