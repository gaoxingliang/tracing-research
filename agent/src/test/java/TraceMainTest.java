import com.zoomphant.agent.trace.TraceMain;
import com.zoomphant.agent.trace.checker.KafkaChecker;

import java.util.HashMap;
import java.util.Map;

public class TraceMainTest {
    public static void main(String[] args) throws InterruptedException {
        Map<String, String> ids = new HashMap<>();
        ids.put("_resourceName", "hello");
        TraceMain.start("./releaselibs", new KafkaChecker(), ids);
        Thread.sleep(1000000);
    }


}
