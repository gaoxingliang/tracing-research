package com.zoomphant.agent.trace.common;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@UtilityClass
public class TraceLog {

    public static void debug(String msg) {
        System.out.println("TRACE :" + msg);
    }

    public void info(String msg) {
        System.out.println("TRACE :" + msg);
         // logger().log(Level.INFO, msg);
    }


    private Logger logger() {
        return Logger.getGlobal();
    }

    public void error(String msg, Throwable e) {
        System.out.println("TRACE : Found error: " + ExceptionUtils.getStackTrace(e));
        e.printStackTrace();

        LogRecord lr = new LogRecord(Level.SEVERE, msg);
        lr.setThrown(e);
        logger().log(lr);
    }
}
