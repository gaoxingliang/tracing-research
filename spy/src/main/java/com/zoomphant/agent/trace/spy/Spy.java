package com.zoomphant.agent.trace.spy;

import java.util.HashSet;
import java.util.Set;

/**
 * this classes is used to make sure only the agent class is loaded only once per agent class
 *
 * This class will be loaded by bootstrap class loader.
 */
public class Spy {

    private static final Set<String> inited = new HashSet<>();

    public static boolean initIfNotExists(String agentClass) {
        synchronized (inited) {
            if (inited.contains(agentClass)) {
                return false;
            }
            inited.add(agentClass);
            return true;
        }
    }

}
