package four.six.ftproxy.server;

import junit.framework.*;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class ServerTests
{
    TestEchoServer echoServer;

    public int startEchoServer() throws Exception
    {
        int p = 7070;
        while (p < 8070)
        {
            echoServer = new TestEchoServer();
            echoServer.setPort(p);
            echoServer.start();
            Thread.sleep(1000);
            if (echoServer.isRunning())
                break;
            p++;
        }
        assertTrue(echoServer.isRunning());
        return p;
    }

    public void stopEchoServer()
    {
        assertTrue(echoServer != null && echoServer.isRunning());
        System.out.println("About to interrupt echo server");
        echoServer.interrupt();
    }

    @Test
    public void doTest1() throws Exception
    {
    }
}
