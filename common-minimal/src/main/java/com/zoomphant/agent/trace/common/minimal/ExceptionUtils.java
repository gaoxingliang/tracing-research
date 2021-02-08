package com.zoomphant.agent.trace.common.minimal;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
    public static String fullStack(Throwable e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static Throwable rootCause(Throwable e) {
        Throwable x = e;
        while (x.getCause() != null) {
            x = x.getCause();
        }
        return x;
    }
}
