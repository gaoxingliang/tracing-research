import com.zoomphant.agent.trace.TraceMain;
import com.zoomphant.agent.trace.checker.SQLChecker;

import java.util.HashMap;

public class TraceMainTest {
    public static void main(String[] args) throws InterruptedException {
        TraceMain.start("./releaselibs", new SQLChecker(), new HashMap<>());
        Thread.sleep(1000000);
    }


}
