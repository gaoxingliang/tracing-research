package com.zoomphant.agent.trace.common;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;

public class JmxUtils {
    public static final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    public static String getValue(String objectName, String attr) {
        try {
            return mBeanServer.getAttribute(new ObjectName(objectName), attr).toString();
        } catch (Exception e) {
            return null;
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
        System.out.println(r);

    }


}
