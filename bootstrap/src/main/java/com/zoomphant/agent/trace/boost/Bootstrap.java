package com.zoomphant.agent.trace.boost;

import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.minimal.TraceOption;
import com.zoomphant.agent.trace.common.minimal.TracerType;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.JarFile;

public class Bootstrap {

    public static final String SPY_JAR = "spy-0.0.1-all.jar";
    public static final String SPY_CLASS = "com.zoomphant.agent.trace.spy.Spy";

    public static final String install = "install";

    public static void premain(String agentArgs, Instrumentation inst) {
        run(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        run(agentArgs, inst);
    }

    public static final ConcurrentSkipListSet loadedAgents = new ConcurrentSkipListSet();

    public static void run(String agentArgs, Instrumentation instrumentation) {

        TraceLog.info("My classloader:" + TraceLog.class.getClassLoader());
        TraceLog.info("Current classloader:" + Bootstrap.class.getClassLoader());

        // parse the dest fill
        Map<String, String> options = TraceOption.parseOptions(agentArgs);
        TracerType r = TracerType.valueOf(TraceOption.getOption(options, TraceOption.TRACER_TYPE));
        String agentFile = TraceOption.getOption(options, TraceOption.JARFILE);
        String agentClass = r.getMainClass();
        try {
            // here we use a global system loader to
            // make sure only one time the class is loaded.
            ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
            Class<?> spyClass = null;
            if (parent != null) {
                try {
                    spyClass = parent.loadClass(SPY_CLASS);
                } catch (Throwable e) {
                    // ignore
                }
            }
            if (spyClass == null) {
                File spyJarFile = new File(new File(agentFile).getParent(), SPY_JAR);
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));
                spyClass = parent.loadClass(SPY_CLASS);
            }
            boolean success = (boolean) spyClass.getMethod("initIfNotExists", String.class).invoke(null, agentClass);
            if (!success) {
                TraceLog.info("The class already register " + agentClass);
                return;
            }
            instrumentation.appendToSystemClassLoaderSearch(new JarFile(new File(agentFile)));
            StandaloneAgentClassloader arthasClassLoader = new StandaloneAgentClassloader(
                    new URL[] {new File(agentFile).toURI().toURL()});

            ClassLoader.getSystemClassLoader().loadClass(agentClass);

            Class c = arthasClassLoader.loadClass(agentClass);
            Constructor con = c.getConstructor(String.class, Instrumentation.class, ClassLoader.class);
            con.newInstance(agentArgs, instrumentation, arthasClassLoader);
            TraceLog.info("Success install " + agentClass);
        }
        catch (Exception e) {
            TraceLog.error("Error when loading " + agentClass, e);
        }

    }
}
