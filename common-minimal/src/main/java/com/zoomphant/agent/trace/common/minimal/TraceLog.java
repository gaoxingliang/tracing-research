package com.zoomphant.agent.trace.common.minimal;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class TraceLog {

    public static void debug(String msg) {
        System.out.println(new Date() + Thread.currentThread().getName() + " TRACE :" + msg);
    }

    public static void info(String msg) {
        System.out.println(new Date() + Thread.currentThread().getName() + " TRACE :" + msg);
    }
    public static void warn(String msg) {
        System.out.println(new Date() + Thread.currentThread().getName() + " TRACE :" + msg);
    }


    public static void error(String msg, Throwable e) {
        System.out.println(new Date() + Thread.currentThread().getName() + " TRACE : Found error: " + msg + " "+ getStackTrace(e));
        // e.printStackTrace();

//        LogRecord lr = new LogRecord(Level.SEVERE, msg);
//        lr.setThrown(e);
//        logger().log(lr);
    }


    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
