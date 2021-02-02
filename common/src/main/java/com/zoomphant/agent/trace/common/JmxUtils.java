package com.zoomphant.agent.trace.common;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JmxUtils {
    public static final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    public static String getValue(String objectName, String attr) {
        try {
            return mBeanServer.getAttribute(new ObjectName(objectName), attr).toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static List<String> getNodes(String objectName, String attr) {
        try {
            return mBeanServer.queryNames(new ObjectName (objectName), null).stream()
                    .map(o -> o.getKeyProperty(attr)).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    public static void main(String[] args) throws Exception {
        // remote test
        JMXServiceURL url =
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc =
                jmxc.getMBeanServerConnection();
        ObjectName mxbeanName = new ObjectName ("kafka.server:type=app-info");
        Object r = jmxc.getMBeanServerConnection().getAttribute(mxbeanName, "version");
        ObjectInstance objectInstance = jmxc.getMBeanServerConnection().getObjectInstance(new ObjectName ("kafka.server:type=app-info"));
        System.out.println(mbsc.getAttribute(new ObjectName("kafka.server:type=KafkaServer,name=ClusterId"), "Value"));
        jmxc.getMBeanServerConnection().queryNames(new ObjectName ("kafka.server:id=*,type=app-info"), null).iterator().next().getKeyProperty("id");


        System.out.println(r);

    }


}
