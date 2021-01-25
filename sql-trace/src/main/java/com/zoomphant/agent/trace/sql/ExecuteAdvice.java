package com.zoomphant.agent.trace.sql;

import brave.Span;
import com.zoomphant.agent.trace.common.BasicMain;
import com.zoomphant.agent.trace.common.TraceLog;
import net.bytebuddy.asm.Advice;

import java.sql.SQLException;
import java.sql.Statement;

// https://medium.com/@lnishada/introduction-to-byte-buddy-advice-annotations-48ac7dae6a94
public class ExecuteAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    static Span enter(@Advice.This Statement statement,
                      @Advice.Argument(0) String sql){
        // System.out.println("Got sql " + sql);
        TraceLog.info("Got sql " + sql);
        // target remote url:
        try {
            String url = statement.getConnection().getMetaData().getURL();
            return BasicMain.HOLDER.get(BasicMain.SQL).getRecorder().recordStart("execute", "", url, "sql.query", sql);
        }
        catch (SQLException throwables) {
            return null;
        }

    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable th){
        if (span != null) {
            BasicMain.HOLDER.get(BasicMain.SQL).getRecorder().recordFinish(span, th == null);
        }
    }


}
