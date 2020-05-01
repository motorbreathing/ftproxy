package four.six.ftproxy.server;

import junit.framework.*;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.client.TestClient;

public class ServerTests
{
    private final int echoServerPortStart = 9090;
    private final int echoServerPortEnd = 10090;
    private final int proxyServerPortStart = 8080;
    private final int proxyServerPortEnd = 9080;
    private final int serverStartTimeoutMillis = 1000;
    private final int readTimeoutMillis = 5000;

    TestEchoServer echoServer;
    TestProxyServer proxyServer;

    public int startEchoServer(boolean ssl) throws Exception
    {
        int p = echoServerPortStart;
        while (p < echoServerPortEnd)
        {
            echoServer = new TestEchoServer();
            if (ssl)
                echoServer.enableSSL();
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

    public void stopEchoServer()
    {
        if (echoServer != null && echoServer.isRunning())
            echoServer.interrupt();
    }

    public int startProxyServer(boolean ssl) throws Exception
    {
        int p = proxyServerPortStart;
        while (p < proxyServerPortEnd)
        {
            proxyServer = new TestProxyServer();
            if (ssl)
                proxyServer.enableSSL();
            proxyServer.setPort(p);
            proxyServer.start();
            Thread.sleep(serverStartTimeoutMillis);
            if (proxyServer.isRunning())
                break;
            p++;
        }
        assertTrue(proxyServer.isRunning());
        return p;
    }

    public void stopProxyServer()
    {
        if (proxyServer != null && proxyServer.isRunning())
            proxyServer.interrupt();
    }

    @Test
    public void testEchoServer() throws Exception
    {
        startEchoServer(true);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, echoServer.getPort(), true);
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
        stopEchoServer();
        c.disconnect();
    }

    public void testProxySSLScenarios(boolean cf, boolean pf, boolean sf) throws Exception
    {
        int echoServerPort = startEchoServer(sf);
        System.setProperty(Util.REMOTE_PORT_KEY, Integer.toString(echoServerPort));
        int proxyServerPort = startProxyServer(pf);
        if (sf)
            proxyServer.enableServerSSL();
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, proxyServerPort, cf);
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
        stopEchoServer();
        stopProxyServer();
        c.disconnect();
    }

    @Test
    public void testProxyServer() throws Exception
    {
        testProxySSLScenarios(true, true, true);
        testProxySSLScenarios(false, false, true);
        testProxySSLScenarios(true, true, false);
    }
}
