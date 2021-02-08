package com.zoomphant.agent.trace.sql;

import com.zoomphant.agent.trace.common.minimal.MainHolders;
import com.zoomphant.agent.trace.common.minimal.Span;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import net.bytebuddy.asm.Advice;

import java.sql.Statement;

/**
 * !DO NOT IMPORT ANY CLASSES NOT UNDER {@link com.zoomphant.agent.trace.common.minimal}
 */
// https://medium.com/@lnishada/introduction-to-byte-buddy-advice-annotations-48ac7dae6a94
public class ExecuteAdvice {

    public static final String NAME = "com.zoomphant.agent.trace.sql.SqlMain";

    @Advice.OnMethodEnter(suppress = Throwable.class)
    static Span enter(@Advice.This Statement statement,
                      @Advice.AllArguments Object[] args) {
        // target remote url:
        try {
            String url = statement.getConnection().getMetaData().getURL();
            return MainHolders.get(NAME).getRecorder().recordStart("execute", url, "sql.query",
                    args == null || args.length == 0 ? "" : String.valueOf(args[0]));
        }
        catch (Throwable throwables) {
            TraceLog.error("Fail ed", throwables);
            return null;
        }

    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable th) {
        if (span != null) {
            MainHolders.get(NAME).getRecorder().recordFinish(span, th);
        }
    }


}
