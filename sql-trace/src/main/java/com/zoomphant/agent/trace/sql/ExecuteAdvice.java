package com.zoomphant.agent.trace.sql;

import brave.Span;
import com.zoomphant.agent.trace.common.BasicMain;
import com.zoomphant.agent.trace.common.TracerType;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import net.bytebuddy.asm.Advice;

import java.sql.Statement;
import java.util.Arrays;

// https://medium.com/@lnishada/introduction-to-byte-buddy-advice-annotations-48ac7dae6a94
public class ExecuteAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    static Span enter(@Advice.This Statement statement,
                      @Advice.AllArguments Object[] args){
        TraceLog.info("Got sql " + Arrays.asList(args));
        // target remote url:
        try {
            String url = statement.getConnection().getMetaData().getURL();
            return BasicMain.HOLDER.get(TracerType.SQL).getRecorder().recordStart("execute", url, "sql.query", args == null || args.length == 0 ? "" : String.valueOf(args[0]));
        }
        catch (Throwable throwables) {
            return null;
        }

    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable th){
        if (span != null) {
            BasicMain.HOLDER.get(TracerType.SQL).getRecorder().recordFinish(span, th);
        }
    }


}
