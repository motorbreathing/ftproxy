package four.six.ftproxy.server;

import junit.framework.*;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.client.TestClient;

public class ServerTests
{
    private final int serverPortStart = 7070;
    private final int serverPortEnd = 8070;
    private final int serverStartTimeoutMillis = 1000;
    private final int readTimeoutMillis = 5000;

    TestEchoServer echoServer;

    public int startEchoServer() throws Exception
    {
        int p = serverPortStart;
        while (p < serverPortEnd)
        {
            echoServer = new TestEchoServer();
            echoServer.setPort(p);
            echoServer.start();
            Thread.sleep(serverStartTimeoutMillis);
            if (echoServer.isRunning())
                break;
            p++;
        }
        assertTrue(echoServer.isRunning());
        return p;
    }

    @Test
    public void testEchoServer() throws Exception
    {
        startEchoServer();
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, echoServer.getPort());
        String s1 = "hello1";
        c.write(s1 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s1));
        String s2 = "hello2";
        c.write(s2 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s2));
        String s3 = "hello3";
        c.write(s3 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s3));
        String s4 = "hello4";
        c.write(s4 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s4));
        // Shuts down the echo server
        c.write("quit" + Util.CRLF);
        c.disconnect();
    }
}
