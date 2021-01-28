import com.zoomphant.agent.trace.TraceMain;

public class TraceMainTest {
    public static void main(String[] args) throws InterruptedException {
        TraceMain.start("./releaselibs");
        Thread.sleep(1000000);
    }


}
