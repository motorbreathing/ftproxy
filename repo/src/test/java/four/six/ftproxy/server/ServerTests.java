package four.six.ftproxy.server;

import junit.framework.*;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.util.TestUtil;
import four.six.ftproxy.client.TestClient;
import four.six.ftproxy.ftp.FTPAuthCommand;

public class ServerTests
{
    private final int echoServerPortStart = 9090;
    private final int echoServerPortEnd = 10090;
    private final int ftpServerPortStart = 5050;
    private final int ftpServerPortEnd = 6050;
    private final int proxyServerPortStart = 8080;
    private final int proxyServerPortEnd = 9080;
    private final int serverStartTimeoutMillis = 1000;
    private final int readTimeoutMillis = 5000;

    TestEchoServer echoServer;
    TestEchoServer ftpServer;
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

    public int startFTPServer(boolean ssl) throws Exception
    {
        int p = ftpServerPortStart;
        while (p < ftpServerPortEnd)
        {
            ftpServer = new TestFTPServer();
            if (ssl)
                ftpServer.enableSSL();
            ftpServer.setPort(p);
            ftpServer.start();
            Thread.sleep(serverStartTimeoutMillis);
            if (ftpServer.isRunning())
                break;
            p++;
        }
        assertTrue(ftpServer.isRunning());
        return p;
    }


    public void stopEchoServer()
    {
        if (echoServer != null && echoServer.isRunning())
            echoServer.interrupt();
    }

    public void stopFTPServer()
    {
        if (ftpServer != null && ftpServer.isRunning())
            ftpServer.interrupt();
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

    public void testEchoServerSSLNegative1() throws Exception
    {
        boolean nullPointerException = false;
        boolean otherException = false;

        TestClient c = new TestClient();

        try {
            startEchoServer(false);
            c.connect(Util.THIS_HOST, echoServer.getPort(), true);
            String s1 = "hello";
            c.write(s1 + Util.CRLF);
            // This should trigger an exception from the SSL layer
            assertTrue(c.readLine(readTimeoutMillis).equals(s1) == false);
        } catch (NullPointerException e) {
            // This should not happen
            nullPointerException = true;
        } catch (Exception e) {
            // This should happen - an SSL exception (eg: SSL Engine already
            // closed blah blah)
            otherException = true;
        } finally {
            stopEchoServer();
            c.disconnect();
        }

        assertTrue(nullPointerException == false);
        assertTrue(otherException == true);
    }

    public void testEchoServerSSLNegative2() throws Exception
    {
        boolean nullPointerException = false;
        boolean otherException = false;

        TestClient c = new TestClient();

        try {
            startEchoServer(true);
            c.connect(Util.THIS_HOST, echoServer.getPort(), false);
            String s1 = "hello";
            c.write(s1 + Util.CRLF);
            // This ought to generate an NPE
            assertTrue(c.readLine(readTimeoutMillis).equals(s1));
        } catch (NullPointerException e) {
            // This should happen
            nullPointerException = true;
        } catch (Exception e) {
            // This should not happen
            otherException = true;
        } finally {
            stopEchoServer();
            c.disconnect();
        }

        assertTrue(nullPointerException);
        assertTrue(otherException == false);
    }

    public void testEchoServerSSL(boolean serverSSLEnabled, boolean clientSSLEnabled) throws Exception
    {
        startEchoServer(serverSSLEnabled);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, echoServer.getPort(), clientSSLEnabled);
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

    public void testEchoServerExplicitSSL() throws Exception
    {
        startEchoServer(false);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, echoServer.getPort(), false);
        String s1 = "hello1";
        c.write(s1 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s1));

        c.enableSSL();
        echoServer.enableExplicitSSL();

        String s2 = "hello2";
        c.write(s2 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s2));
        stopEchoServer();
        c.disconnect();
    }

    public void testEchoServer() throws Exception
    {
        testEchoServerSSL(false, false);
        testEchoServerSSL(true, true);

        testEchoServerExplicitSSL();

        testEchoServerSSLNegative1();
        testEchoServerSSLNegative2();

        testEchoServerExplicitSSL();
    }

    public void testProxyServerSSL(boolean clientSSLEnabled, boolean proxySSLEnabled, boolean serverSSLEnabled) throws Exception
    {
        int echoServerPort = startEchoServer(serverSSLEnabled);
        System.setProperty(Util.REMOTE_PORT_KEY, Integer.toString(echoServerPort));
        int proxyServerPort = startProxyServer(proxySSLEnabled);
        if (serverSSLEnabled)
            proxyServer.enableServerSSL();
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, proxyServerPort, clientSSLEnabled);
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

    public void testProxyServerExplicitSSL() throws Exception
    {
        // SSL is disabled all around, initially
        int echoServerPort = startEchoServer(false);
        System.setProperty(Util.REMOTE_PORT_KEY, Integer.toString(echoServerPort));
        int proxyServerPort = startProxyServer(false);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, proxyServerPort, false);
        String s1 = "hello";
        c.write(s1 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s1));

        // Explicitly/Late enablement of SSL all around
        c.enableSSL();
        echoServer.enableExplicitSSL();
        proxyServer.enableClientSSL();
        proxyServer.enableServerSSL();

        // Further communication is secure
        String s2 = "securehello";
        c.write(s2 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s2));

        stopEchoServer();
        stopProxyServer();
        c.disconnect();
    }

    public void testProxyServer() throws Exception
    {
        testProxyServerSSL(true, true, true);
        testProxyServerSSL(false, false, true);
        testProxyServerSSL(true, true, false);
        testProxyServerExplicitSSL();
    }

    public void testFTPServerExplicitSSL(boolean negativeTest) throws Exception
    {
        int ftpServerPort = startFTPServer(false);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, ftpServerPort, false);
        String s1 = "hello";
        c.write(s1 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(TestUtil.withoutCRLF(TestFTPHandler.GENERIC_RESPONSE_200_STR)));

        c.requestExplicitSSL();
        assertTrue(c.readLine(readTimeoutMillis).equals(TestUtil.withoutCRLF(FTPAuthCommand.RESPONSE_STR)));

        if (!negativeTest)
            c.enableSSL();
        c.write(s1 + Util.CRLF);
        boolean npe = false;
        try {
            assertTrue(c.readLine(readTimeoutMillis).equals(TestUtil.withoutCRLF(TestFTPHandler.GENERIC_RESPONSE_200_STR)));
        } catch (NullPointerException e) {
            npe = true;
        }

        if (negativeTest)
            assertTrue(npe == true);
        else
            assertTrue(npe == false);
        
        stopFTPServer();
        c.disconnect();
    }

    public void testFTPServer() throws Exception
    {
        testFTPServerExplicitSSL(false);
        // Negative test
        testFTPServerExplicitSSL(true);
    }

    @Test
    public void runServerTests() throws Exception
    {
        // No Proxy; client <-> echo server
        testEchoServer();
        // client <-> proxy <-> echo server
        testProxyServer();
        // No Proxy; client <-> ftp server
        testFTPServer();
    }
}
