package com.zoomphant.agent.trace.sql;

import com.zoomphant.agent.trace.common.MainHolders;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import com.zoomphant.agent.trace.common.rewrite.Span;
import net.bytebuddy.asm.Advice;

import java.sql.Statement;
import java.util.Arrays;

// https://medium.com/@lnishada/introduction-to-byte-buddy-advice-annotations-48ac7dae6a94
public class ExecuteAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    static Span enter(@Advice.Origin Object ori,  @Advice.This Statement statement,
                      @Advice.AllArguments Object[] args) {
        TraceLog.debug("Got sql " + Arrays.asList(args));
        // target remote url:
        try {
            String url = statement.getConnection().getMetaData().getURL();
            TraceLog.info("here " + MainHolders.class.getClassLoader() + " and target class:" + ori.getClass().getClassLoader());
            return MainHolders.get(SqlMain.NAME).getRecorder().recordStart("execute", url, "sql.query",
                    args == null || args.length == 0 ? "" : String.valueOf(args[0]));
        }
        catch (Throwable throwables) {
            TraceLog.error("Jusr erroor " + MainHolders.mains + " hereme ", throwables);
            return null;
        }

    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable th) {
        if (span != null) {
            MainHolders.get(SqlMain.NAME).getRecorder().recordFinish(span, th);
        }
    }


}
