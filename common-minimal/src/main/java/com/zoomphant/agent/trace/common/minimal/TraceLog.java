package com.zoomphant.agent.trace.common.minimal;

import lombok.experimental.UtilityClass;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@UtilityClass
public class TraceLog {

    public static void debug(String msg) {
        System.out.println(new Date() + " TRACE :" + msg);
    }

    public void info(String msg) {
        System.out.println(new Date() +" TRACE :" + msg);
    }
    public void warn(String msg) {
        System.out.println(new Date() +" TRACE :" + msg);
    }


    private Logger logger() {
        return Logger.getGlobal();
    }

    public void error(String msg, Throwable e) {
        System.out.println(new Date() +" TRACE : Found error: " + getStackTrace(e));
        e.printStackTrace();

        LogRecord lr = new LogRecord(Level.SEVERE, msg);
        lr.setThrown(e);
        logger().log(lr);
    }


    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
