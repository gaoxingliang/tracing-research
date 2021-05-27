package com.zoomphant.agent.trace.zookeeper;

import com.zoomphant.agent.trace.common.minimal.ContainerDiscovery;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.jmx.JMXBaseMain;
import com.zoomphant.agent.trace.jmx.JmxUtils;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ZookeeperMain extends JMXBaseMain {

    private String zkMode; // standalone or replica
    private String zkReplicaId;
    private String zkQuroumAddr;
    private String zkElecAddr;
    private String zkClientAddr;

    private String zkStandalonePort;

    public ZookeeperMain(String agentArgs, Instrumentation inst, ClassLoader cl) {
        super(agentArgs, inst, cl);
    }

    @Override
    public void install() {
        try {
            Set<ObjectInstance> allInstances = JmxUtils.mBeanServer.queryMBeans(new ObjectName("*:*"), null);
            /**
             * first step got the replica id
             */
            String zkDomain = "org.apache.ZookeeperService";
            String zkBean = "org.apache.zookeeper.server.quorum.QuorumBean";
            String zkRemote = "org.apache.zookeeper.server.quorum.RemotePeerBean";
            Optional<ObjectInstance> instanceOp = allInstances.stream().filter(instance -> instance.getObjectName().getDomain().equalsIgnoreCase(zkDomain)
                    && instance.getClassName().equalsIgnoreCase(zkBean)).findFirst();
            if (instanceOp.isPresent()) {
                ObjectInstance objectInstance = instanceOp.get();
                Hashtable<String, String> props = objectInstance.getObjectName().getKeyPropertyList();
                String replicaId = props.values().stream().filter(value -> value.startsWith("ReplicatedServer_id")).findFirst().get();
                zkMode = "replica";
                zkReplicaId = replicaId.substring("ReplicatedServer_id".length());
                ObjectInstance remoteInstance = allInstances.stream().filter(instance -> instance.getObjectName().getDomain().equalsIgnoreCase(zkDomain)
                        && instance.getClassName().equals(zkRemote)).findFirst().get();
                Hashtable<String, String> remoteprops = remoteInstance.getObjectName().getKeyPropertyList();
                String remotereplicaId = remoteprops.values().stream().filter(value -> value.startsWith("replica.")).findFirst().get();
                AttributeList attrs = JmxUtils.mBeanServer.getAttributes(
                        new ObjectName(String.format("org.apache.ZooKeeperService:name0=%s,name1=%s", "ReplicatedServer_id" + zkReplicaId, remotereplicaId)),
                        new String[]{"QuorumAddress", "ElectionAddress", "ClientAddress"});
                zkQuroumAddr = ((Attribute) attrs.get(0)).getValue().toString();
                zkElecAddr = ((Attribute) attrs.get(1)).getValue().toString();
                zkClientAddr = ((Attribute) attrs.get(2)).getValue().toString();
            }
            else {
                zkMode = "standalone";
//                        // must be standalone
//                        String standalone = props.values().stream().filter(value -> value.startsWith("StandaloneServer_")).findFirst().get();
//                        zkMode = "standalone";
//                        zkStandalonePort = standalone.substring("StandaloneServer_".length());
//                    }
//                    TraceLog.info("Found zk " + zkMode);
//                    return;
            }
        }
        catch (Exception e) {
            TraceLog.error("Fail to scrape zk ", e);
        }
    }

    @Override
    protected Map<String, String> getOtherReportingHeaders() {
        Map<String, String> map =  super.getOtherReportingHeaders();
        map.put("zkMode", zkMode);
        if (zkMode.equals("replica")) {
            map.put("zkReplicaId", zkReplicaId);
            map.put("zkClientAddr", zkClientAddr);
        } else {
            map.put("zkStandalonePort", zkStandalonePort);
        }
        return map;
    }

    @Override
    protected ContainerDiscovery getContainerDiscovery() {
        Map<ContainerDiscovery.ProcessTypeLabel, String> adMap = new HashMap<>(4);
        if (zkReplicaId != null) {
            adMap.put(ContainerDiscovery.ProcessTypeLabel.zk_replica, zkReplicaId);
        }
        if (zkMode != null) {
            adMap.put(ContainerDiscovery.ProcessTypeLabel.zk_mode, zkMode);
        }
        if (zkClientAddr != null) {
            adMap.put(ContainerDiscovery.ProcessTypeLabel.zk_clientAddr, zkClientAddr);
        }

        if (zkElecAddr != null) {
            adMap.put(ContainerDiscovery.ProcessTypeLabel.zk_elecAddr, zkElecAddr);
        }

        if (zkQuroumAddr != null) {
            adMap.put(ContainerDiscovery.ProcessTypeLabel.zk_quorumAddr, zkQuroumAddr);
        }

        if (adMap.isEmpty()) {
            return null;
        }
        ContainerDiscovery containerDiscovery = new ContainerDiscovery();
        containerDiscovery.getProcessTypeMap().put(ContainerDiscovery.ProcessType.ZOOKEEPER, adMap);
        return containerDiscovery;
    }

    @Override
    protected String getConfigYaml() {
        return "zookeeperJmx.yaml";
    }
}

