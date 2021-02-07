import com.zoomphant.agent.trace.TraceMain;
import com.zoomphant.agent.trace.checker.SQLChecker;

import java.util.HashMap;
import java.util.Map;

public class TraceMainTest {
    public static void main(String[] args) throws InterruptedException {
        Map<String, String> ids = new HashMap<>();
        ids.put("_resourceName", "hello");
        int pid = 5199;
        TraceMain.testPid = pid;
        TraceMain.start("./releaselibs", new SQLChecker(), ids);

        Thread.sleep(1000000);
    }


}
