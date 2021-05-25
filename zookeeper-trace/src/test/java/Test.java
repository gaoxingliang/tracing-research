import com.zoomphant.agent.trace.common.minimal.utils.FileUtils;
import io.prometheus.jmx.shaded.io.prometheus.jmx.JmxCollector;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws Exception {
        String filePath = "kafka2_0_JMX.yaml";
        String fileContent = FileUtils.getFile(Test.class.getClassLoader(), filePath);
        String body = "jmxUrl: service:jmx:rmi:///jndi/rmi://:9999/jmxrmi\n" + fileContent;


        JmxCollector jmxCollector = new JmxCollector(body);
        jmxCollector.collect().forEach(l -> {
            System.out.println(l.name + " " + l.samples.get(0));
        });

        JMXServiceURL url =
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        listOutallMbeans(jmxc.getMBeanServerConnection());

    }

    public static void listOutallMbeans(MBeanServerConnection server) throws Exception {
        // MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        Set<ObjectInstance> instances = server.queryMBeans(null, null);

        instances.forEach(instance -> {
            ;
            System.out.println("MBean Found:");
            System.out.println("Class Name:" + instance.getClassName());
            System.out.println("Object Name:" + instance.getObjectName());
            System.out.println("****************************************");
        });
    }

}


