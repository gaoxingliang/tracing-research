package com.zoomphant.agent.trace.boost;

import com.zoomphant.agent.trace.common.minimal.BasicMain;
import com.zoomphant.agent.trace.common.minimal.MainHolders;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.minimal.TraceOption;
import com.zoomphant.agent.trace.common.minimal.TracerType;
import com.zoomphant.agent.trace.common.minimal.utils.StringUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarFile;

public class Bootstrap {

    public static final String SPY_CLASS = "com.zoomphant.agent.trace.spy.Spy";

    public static void premain(String agentArgs, Instrumentation inst) {
        run(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        run(agentArgs, inst);
    }

    public static void run(String agentArgs, Instrumentation instrumentation) {

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
                File spyJarFile = new File(TraceOption.getOption(options, TraceOption.SPY_JAR));
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));
                // only add once....
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(new File(TraceOption.getOption(options, TraceOption.BOOTSTRAP_JAR))));
                spyClass = parent.loadClass(SPY_CLASS);
            }
            // 0: init success.  1: overwrite a existed agents.  2: the agent args is same and should ignore this time.
            int initResult = (int) spyClass.getMethod("initIfNotExists", String.class, String.class).invoke(null, agentClass, agentArgs);
            // TraceLog.info("The result for " + agentClass + " is" + initResult);
            if (initResult == 2) {
                TraceLog.info("The class already register " + agentClass);
                return;
            } else if (initResult == 1) {
                MainHolders.flushAgentArgs(agentClass, agentArgs);
                TraceLog.info("The class args are flushed " + agentClass);
                return;
            }

            String requiredClass = r.getRequiredClass();
            ClassLoader requiredClassLoader = null;
            if (!StringUtils.isEmpty(requiredClass)) {
                // try to check whether Filethe classloader is already loaded this class
                requiredClassLoader = getClassloaderOfClass(instrumentation, requiredClass);
                if (requiredClassLoader == null) {
                    TraceLog.info("Not install " + agentClass + " because of require class not loaded" + requiredClass);
                    return;
                }
            }

            // apply the bootstrap jar
            StandaloneAgentClassloader arthasClassLoader = new StandaloneAgentClassloader(new URL[] {
                    new File(TraceOption.getOption(options, TraceOption.BYTE_BUDDY_SHARE_JAR)).toURL(),
                    new File(agentFile).toURL()}, requiredClassLoader);
            arthasClassLoader.loadClass(agentClass);
            Class c = arthasClassLoader.loadClass(agentClass);
            Constructor con = c.getConstructor(String.class, Instrumentation.class, ClassLoader.class);

            Object basicMain = con.newInstance(agentArgs, instrumentation, arthasClassLoader);
            MainHolders.register(agentClass, (BasicMain) basicMain);
            TraceLog.info("Success install " + agentClass);
        }
        catch (Exception e) {
            TraceLog.error("Error when loading " + agentClass, e);
        }

    }


    private static ClassLoader getClassloaderOfClass(Instrumentation instrumentation, String className) {
        try {
            Class [] classes = instrumentation.getAllLoadedClasses();
            for (Class c : classes) {
                try {
                    // starts is okay for some internal classes like org.apache.kafka.Kafka.$Abc
                    if (c.getCanonicalName().startsWith(className)) {
                        ClassLoader cl =  c.getClassLoader();
                        TraceLog.info(String.format("Found the require class %s is loaded by %s", className, cl));
                        return cl;
                    }
                } catch (Throwable e) {
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
