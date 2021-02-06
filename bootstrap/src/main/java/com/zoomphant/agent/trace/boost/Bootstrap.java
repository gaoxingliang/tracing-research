package com.zoomphant.agent.trace.boost;

import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.minimal.TraceOption;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

public class Bootstrap {
    public static final String install = "install";

    public static void premain(String agentArgs, Instrumentation inst) {
        run(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        run(agentArgs, inst);
    }

    public static void run(String agentArgs, Instrumentation instrumentation) {
        // parse the dest fill
        Map<String, String> options = TraceOption.parseOptions(agentArgs);
        String agentFile = TraceOption.getOption(options, TraceOption.JARFILE);
        String agentClass = TraceOption.getOption(options, TraceOption.AGENTCLASS);
        try {
            AttachArthasClassloader arthasClassLoader = new AttachArthasClassloader(
                    new URL[] {new File(agentFile).toURI().toURL()});
            Class c = arthasClassLoader.loadClass(agentClass);
            Method m = c.getMethod(install, String.class, Instrumentation.class);
           // instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(new File(agentFile)));
            TraceLog.info("Found m:" + m);
            m.invoke(null, agentArgs, instrumentation);
            TraceLog.info("Success");
        }
        catch (Exception e) {
            TraceLog.error("Error when loading " + agentClass, e);
        }

    }
}
