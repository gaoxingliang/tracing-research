package com.zoomphant.agent.trace.boost;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class is a standalone agent class loader using below steps:
 *  (1) check whether it's already loaded
 *  (2) if it has additional class loader, try loads from it.
 *  (3) if not, try from the bootstrap class loader
 *  (4) then from the agent jar input file.
 *
 * The additional classloader is used because eg: a spring boot app, the advice is in it's own LaunchedURLClassLoader.
 * while the agent is loaded in the system class loader.
 * We combine this class loader to help the basic main classes searching the advices.
 *
 * This class is refered from :
 * https://github.com/alibaba/arthas/blob/master/arthas-agent-attach/src/main/java/com/taobao/arthas/agent/attach/AttachArthasClassloader.java
 */
public class StandaloneAgentClassloader extends URLClassLoader {

    private final ClassLoader additionalClassloader;

    public StandaloneAgentClassloader(URL[] urls, ClassLoader additionalClassloader) {
        super(urls, ClassLoader.getSystemClassLoader().getParent());
        this.additionalClassloader = additionalClassloader;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 优先从parent（SystemClassLoader）里加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }
        if (additionalClassloader != null) {
            // try load from additional classloader
            try {
                loadedClass = additionalClassloader.loadClass(name);
                return loadedClass;
            } catch (Exception ignore) {
            }
        }
        try {
            loadedClass = this.getParent().loadClass(name);
            return loadedClass;
        } catch (Exception e) {
        }
        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            // ignore
        }
        return super.loadClass(name, resolve);
    }
}
