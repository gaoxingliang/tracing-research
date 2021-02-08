package com.zoomphant.agent.trace.common.minimal.utils;

public class StringUtils {
    public static String abbr(String s, int max) {
        if (isEmpty(s)) {
            return s + "";
        }
        return s.substring(0, Math.min(s.length(), max));
    }
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
