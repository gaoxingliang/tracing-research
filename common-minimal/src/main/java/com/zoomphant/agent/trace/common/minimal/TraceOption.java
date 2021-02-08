package com.zoomphant.agent.trace.common.minimal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TraceOption {
    // central agent address
    public static final String CENTRALPORT = "_centralport";
    public static final String CENTRALHOST = "_centralhost";
    public static final String CONTAINER = "_container";
    public static final String NODENAME = "_node";
    public static final String PID = "_pid";
    public static final String TRACER_TYPE = "_tracer";
    public static final String JARFILE = "_jar"; // the agent jar
    public static final String BOOTSTRAP_JAR = "_bootstrap_jar";
    public static final String SPY_JAR = "_spy_jar";


    public static final String REPORTING_HEADER_PREFIX = "__r_";

    public static Map<String, String> buildReportingHeaders(Map<String, String> map) {
        Map<String, String> h = new HashMap<>(map.size());
        map.forEach((k,v) -> h.put(REPORTING_HEADER_PREFIX + k, v));
        return h;
    }

    public static Map<String, String> filterReportingHeaders(Map<String, String> map) {
        Map<String, String> h = new HashMap<>(map.size());
        map.entrySet().stream().filter(e -> e.getKey().startsWith(REPORTING_HEADER_PREFIX))
                .forEach(e -> h.put(e.getKey(), e.getValue()));
        return h;
    }


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
        StringBuilder sb = new StringBuilder();
        if (!options.isEmpty()) {
            options.forEach((k,v) -> sb.append(k).append("=").append(v).append("##"));
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }
}
