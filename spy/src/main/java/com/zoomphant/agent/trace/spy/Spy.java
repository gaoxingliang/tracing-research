package com.zoomphant.agent.trace.spy;

import java.util.HashMap;
import java.util.Map;

/**
 * this classes is used to make sure only the agent class is loaded only once per agent class
 *
 * This class will be loaded by bootstrap class loader.
 */
public class Spy {

    private static final Map<String, String> inited = new HashMap<>();

    /**
     *
     * @param agentClass
     * @param agentArgs
     * @return  0: init success.  1: overwrite a existed agents.  2: the agent args is same and should ignore this time.
     */
    public static int initIfNotExists(String agentClass, String agentArgs) {
        synchronized (inited) {
            String existedArgs = inited.get(agentClass);
            if (existedArgs == null) {
                inited.put(agentClass, agentArgs);
                return 0;
            } else if (!existedArgs.equals(agentArgs)) {
                inited.put(agentClass, agentArgs);
                return 1;
            } else {
                return 2;
            }
        }
    }

}
