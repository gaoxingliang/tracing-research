import com.zoomphant.agent.trace.AttachTask;
import com.zoomphant.agent.trace.TraceMain;
import com.zoomphant.agent.trace.common.minimal.TraceOption;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ExampleTraceSQL {


    public static void main(String[] args) throws Exception {
        // HostServer.start(HostServer.DEFAULT_PORT);
        String x = "execute|executeUpdate|executeQuery";
        System.out.println(Pattern.matches(x, "executeQuery"));

        long pid = 45806;
        //            /Users/edward/projects/forked/tracing-research/sql-trace/build/libs/sql-trace-0.0.1-all.jar
        String jar = new File("./releaselibs/sql-trace-0.0.1-all.jar").getCanonicalPath();
        Map<String, String> options = new HashMap<>();
        options.put(TraceOption.CENTRALHOST, "127.0.0.1");
        options.put(TraceOption.CENTRALPORT, "9411");
        options.put(TraceOption.CONTAINER, "MOCKED");
        options.put(TraceOption.JARFILE, jar);
        options.put(TraceOption.AGENTCLASS, "com.zoomphant.agent.trace.sql.SqlMain");
        String boostjar = new File("./releaselibs/" + TraceMain.BOOTSTRAP_JAR).getCanonicalPath();

        Thread th = new Thread(new AttachTask(pid, boostjar, TraceOption.renderOptions(options)));
        th.start();

        // _centralhost=127.0.0.1##_host=127.0.0.1##_centralport=9411##_port=19234
        Thread.sleep(1000000);
    }
}
