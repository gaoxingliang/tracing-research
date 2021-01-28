import com.zoomphant.agent.trace.AttachTask;
import com.zoomphant.agent.trace.HostServer;
import com.zoomphant.agent.trace.common.TraceOption;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ExampleTraceSQL {


    public static void main(String[] args) throws Exception {
        // HostServer.start(HostServer.DEFAULT_PORT);
        String x = "execute|executeUpdate|executeQuery";
        System.out.println(Pattern.matches(x, "executeQuery"));

        long pid = 82424;
        //            /Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar
        String jar = new File("./releaselibs/sql-trace-0.0.1-all.jar").getCanonicalPath();
        Map<String, String> options = new HashMap<>();
        options.put(TraceOption.HOST, "127.0.0.1");
        options.put(TraceOption.PORT, HostServer.DEFAULT_PORT + "");
        options.put(TraceOption.CENTRALHOST, "127.0.0.1");
        options.put(TraceOption.CENTRALPORT, "9411");
        options.put(TraceOption.CONTAINER, "MOCKED");

        Thread th = new Thread(new AttachTask(pid, jar, options));
        th.start();

        // _centralhost=127.0.0.1##_host=127.0.0.1##_centralport=9411##_port=19234
        Thread.sleep(1000000);
    }
}
