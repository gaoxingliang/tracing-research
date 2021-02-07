package com.zoomphant.agent.trace.common;

import java.util.concurrent.ConcurrentHashMap;

public class MainHolders {
    public static ConcurrentHashMap<String, BasicMain> mains = new ConcurrentHashMap<>();
    public static BasicMain get(String mainType) {
        return mains.get(mainType);
    }

    public static void register(String mainType, BasicMain main) {
        mains.put(mainType, main);
    }
}
