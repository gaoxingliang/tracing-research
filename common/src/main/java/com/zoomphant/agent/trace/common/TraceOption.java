package com.zoomphant.agent.trace.common;

import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TraceOption {
    // physical NODE
    public static final String PORT = "_port";
    public static final String HOST = "_host";

    // central agent address
    public static final String CENTRALPORT = "_centralport";
    public static final String CENTRALHOST = "_centralhost";


    public static Map<String, String> parseOptions(String agentArgs) {
        if (agentArgs == null) {
            return new HashMap<>();
        }
        Map<String, String> argMap = new HashMap<>();
        String [] args = agentArgs.split("##");
        for (int i = 0; i < args.length; i++) {
            String [] kv = args[i].split("=", 2);
            argMap.put(kv[0], kv[1]);
        }
        return argMap;
    }


    public static String getOption(Map<String, String> options, String op) {
        return Optional.ofNullable(options.get(op)).orElseThrow(() -> new IllegalArgumentException("Invalid key - " + op));
    }

    public static int getOptionInt(Map<String, String> options, String op) {
        return Integer.valueOf(getOption(options, op));
    }

    public static String renderOptions(Map<String, String> options) {
        return Joiner.on("##").join(options.entrySet().stream().map(e -> e.getKey() +"=" + e.getValue()).collect(Collectors.toList()));

    }
}
