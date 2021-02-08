package com.zoomphant.agent.trace.common.minimal.utils;

import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    public static boolean sleepInterruptable(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
            return false;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }
}
