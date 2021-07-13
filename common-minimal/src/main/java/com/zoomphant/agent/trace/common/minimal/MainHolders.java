package com.zoomphant.agent.trace.common.minimal;

import java.util.concurrent.ConcurrentHashMap;

public class MainHolders {
    // the key is the full class name....
    public static ConcurrentHashMap<String, BasicMain> mains = new ConcurrentHashMap<>();
    public static BasicMain get(String mainType) {
        return mains.get(mainType);
    }

    public static void register(String mainType, BasicMain main) {
        mains.put(mainType, main);
    }

    public static void flushAgentArgs(String mainType, String args){
        BasicMain main = get(mainType);
        if (main != null) {
            main.flushAgentArgs(args, false);
        }
    }
}
