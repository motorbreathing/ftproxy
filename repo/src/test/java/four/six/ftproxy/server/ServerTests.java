package four.six.ftproxy.server;

import static org.junit.Assert.assertTrue;

import four.six.ftproxy.client.TestClient;
import four.six.ftproxy.ftp.FTPAuthCommand;
import four.six.ftproxy.util.TestUtil;
import four.six.ftproxy.util.Util;
import junit.framework.*;
import org.junit.Test;

public class ServerTests {
    private final int simpleServerPortStart = 9090;
    private final int simpleServerPortEnd = 10090;
    private final int ftpServerPortStart = 5050;
    private final int ftpServerPortEnd = 6050;
    private final int proxyServerPortStart = 8080;
    private final int proxyServerPortEnd = 9080;
    private final int serverStartTimeoutMillis = 1000;
    private final int readTimeoutMillis = 5000;

    TestSimpleServer simpleServer;
    TestSimpleServer ftpServer;
    TestProxyServer proxyServer;

    public int startSimpleServer(String host, boolean ssl) throws Exception {
        int p = simpleServerPortStart;
        while (p < simpleServerPortEnd) {
            simpleServer = new TestSimpleServer();
            simpleServer.setHost(host);
            if (ssl) simpleServer.enableSSL();
            simpleServer.setPort(p);
            simpleServer.start();
            Thread.sleep(serverStartTimeoutMillis);
            if (simpleServer.isRunning()) break;
            p++;
        }
        assertTrue(simpleServer.isRunning());
        return p;
    }

    public int startSimpleServer(boolean ssl) throws Exception {
        return startSimpleServer(Util.THIS_HOST, ssl);
    }

    public void stopSimpleServer() {
        if (simpleServer != null && simpleServer.isRunning()) simpleServer.interrupt();
    }

    public int startFTPServer(String host, boolean ssl) throws Exception {
        int p = ftpServerPortStart;
        while (p < ftpServerPortEnd) {
            ftpServer = new TestFTPServer();
            ftpServer.setHost(host);
            if (ssl) ftpServer.enableSSL();
            ftpServer.setPort(p);
            ftpServer.start();
            Thread.sleep(serverStartTimeoutMillis);
            if (ftpServer.isRunning()) break;
            p++;
        }
        assertTrue(ftpServer.isRunning());
        return p;
    }

    public int startFTPServer(boolean ssl) throws Exception {
        return startFTPServer(Util.THIS_HOST, ssl);
    }

    public void stopFTPServer() {
        if (ftpServer != null && ftpServer.isRunning()) ftpServer.interrupt();
    }

    public int startProxyServer(String host, boolean ssl) throws Exception {
        int p = proxyServerPortStart;
        while (p < proxyServerPortEnd) {
            proxyServer = new TestProxyServer();
            proxyServer.setHost(host);
            if (ssl) proxyServer.enableSSL();
            proxyServer.setPort(p);
            proxyServer.start();
            Thread.sleep(serverStartTimeoutMillis);
            if (proxyServer.isRunning()) break;
            p++;
        }
        assertTrue(proxyServer.isRunning());
        return p;
    }

    public int startProxyServer(boolean ssl) throws Exception {
        return startProxyServer(Util.THIS_HOST, ssl);
    }

    public void stopProxyServer() {
        if (proxyServer != null && proxyServer.isRunning()) proxyServer.interrupt();
    }

    public void testSimpleServerSSLNegative1() throws Exception {
        boolean nullPointerException = false;
        boolean otherException = false;

        TestClient c = new TestClient();

        try {
            startSimpleServer(false);
            c.connect(Util.THIS_HOST, simpleServer.getPort(), true);
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
            stopSimpleServer();
            c.disconnect();
        }

        assertTrue(nullPointerException == false);
        assertTrue(otherException == true);
    }

    public void testSimpleServerSSLNegative2() throws Exception {
        boolean nullPointerException = false;
        boolean otherException = false;

        TestClient c = new TestClient();

        try {
            startSimpleServer(true);
            c.connect(Util.THIS_HOST, simpleServer.getPort(), false);
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
            stopSimpleServer();
            c.disconnect();
        }

        assertTrue(nullPointerException);
        assertTrue(otherException == false);
    }

    public void testSimpleServerSSL(boolean serverSSLEnabled, boolean clientSSLEnabled)
            throws Exception {
        startSimpleServer(serverSSLEnabled);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, simpleServer.getPort(), clientSSLEnabled);
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
        stopSimpleServer();
        c.disconnect();
    }

    public void testSimpleServerExplicitSSL() throws Exception {
        startSimpleServer(false);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, simpleServer.getPort(), false);
        String s1 = "hello1";
        c.write(s1 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s1));

        c.enableSSL();
        simpleServer.enableExplicitSSL();

        String s2 = "hello2";
        c.write(s2 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s2));
        stopSimpleServer();
        c.disconnect();
    }

    public void testSimpleServer() throws Exception {
        testSimpleServerSSL(false, false);
        testSimpleServerSSL(true, true);

        testSimpleServerExplicitSSL();

        testSimpleServerSSLNegative1();
        testSimpleServerSSLNegative2();

        testSimpleServerExplicitSSL();
    }

    public void testProxyServerSSL(
            boolean clientSSLEnabled, boolean proxySSLEnabled, boolean serverSSLEnabled)
            throws Exception {
        int simpleServerPort = startSimpleServer(serverSSLEnabled);
        Util.setRemotePort(simpleServerPort);
        int proxyServerPort = startProxyServer(proxySSLEnabled);
        if (serverSSLEnabled) proxyServer.enableServerSSL();
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
        stopSimpleServer();
        stopProxyServer();
        c.disconnect();
    }

    public void testProxyServerExplicitSSL() throws Exception {
        // SSL is disabled all around, initially
        int simpleServerPort = startSimpleServer(false);
        Util.setRemotePort(simpleServerPort);
        int proxyServerPort = startProxyServer(false);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, proxyServerPort, false);
        String s1 = "hello";
        c.write(s1 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s1));

        // Explicitly/Late enablement of SSL all around
        c.enableSSL();
        simpleServer.enableExplicitSSL();
        proxyServer.enableClientSSL();
        proxyServer.enableServerSSL();

        // Further communication is secure
        String s2 = "securehello";
        c.write(s2 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s2));

        stopSimpleServer();
        stopProxyServer();
        c.disconnect();
    }

    public void testProxyServerSSLTermination() throws Exception {
        // SSL is disabled all around, initially
        int simpleServerPort = startSimpleServer(false);
        Util.setRemotePort(simpleServerPort);
        int proxyServerPort = startProxyServer(false);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, proxyServerPort, false);
        String s1 = "hello";
        c.write(s1 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s1));

        Util.setSSLTermination(true);
        c.requestExplicitSSL();
        assertTrue(
                c.readLine(readTimeoutMillis).equals(TestUtil.withoutCRLF(FTPAuthCommand.RESPONSE_STR)));

        c.enableSSL();

        // Further communication is secure
        String s2 = "securehello";
        c.write(s2 + Util.CRLF);
        assertTrue(c.readLine(readTimeoutMillis).equals(s2));

        stopSimpleServer();
        stopProxyServer();
        c.disconnect();
    }

    public void testProxyServer() throws Exception {
        testProxyServerSSL(true, true, true);
        testProxyServerSSL(false, false, true);
        testProxyServerSSL(true, true, false);
        testProxyServerExplicitSSL();
        testProxyServerSSLTermination();
    }

    public void testFTPServerExplicitSSL(boolean negativeTest) throws Exception {
        int ftpServerPort = startFTPServer(false);
        TestClient c = new TestClient();
        c.connect(Util.THIS_HOST, ftpServerPort, false);
        String s1 = "hello";
        c.write(s1 + Util.CRLF);
        assertTrue(
                c.readLine(readTimeoutMillis)
                        .equals(TestUtil.withoutCRLF(TestFTPHandler.GENERIC_RESPONSE_200_STR)));

        c.requestExplicitSSL();
        assertTrue(
                c.readLine(readTimeoutMillis).equals(TestUtil.withoutCRLF(FTPAuthCommand.RESPONSE_STR)));

        if (!negativeTest) c.enableSSL();
        c.write(s1 + Util.CRLF);
        boolean npe = false;
        try {
            assertTrue(
                    c.readLine(readTimeoutMillis)
                            .equals(TestUtil.withoutCRLF(TestFTPHandler.GENERIC_RESPONSE_200_STR)));
        } catch (NullPointerException e) {
            npe = true;
        }

        if (negativeTest) assertTrue(npe == true);
        else assertTrue(npe == false);

        stopFTPServer();
        c.disconnect();
    }

    public void testFTPServerData(boolean active, boolean v4, boolean dataSSL) throws Exception {
        int ftpServerPort = startFTPServer(v4 ? Util.THIS_HOST : Util.LOOPBACK_IPV6, false);
        TestClient c = new TestClient();
        c.connect(v4 ? Util.THIS_HOST : Util.LOOPBACK_IPV6, ftpServerPort, false);
        if (dataSSL) {
            c.requestDataSSL();
            String resp = c.readLine(readTimeoutMillis);
            assertTrue(resp.equals(TestUtil.withoutCRLF(TestFTPHandler.PROT_RESPONSE_200_P_STR)));
            c.enableDataSSL();
        }
        String received = null;
        if (active) {
            received = v4 ? c.getFileActiveV4() : c.getFileActiveV6();
        } else {
            received = v4 ? c.getFilePassiveV4() : c.getFilePassiveV6();
        }
        assertTrue(received.equals(TestFTPHandler.JACKAL_STR));

        if (dataSSL) {
            c.requestDataClear();
            String resp = c.readLine(readTimeoutMillis);
            assertTrue(resp.equals(TestUtil.withoutCRLF(TestFTPHandler.PROT_RESPONSE_200_C_STR)));
            c.disableDataSSL();

            if (active) {
                received = v4 ? c.getFileActiveV4() : c.getFileActiveV6();
            } else {
                received = v4 ? c.getFilePassiveV4() : c.getFilePassiveV6();
            }
            assertTrue(received.equals(TestFTPHandler.JACKAL_STR));
        }

        stopFTPServer();
        c.disconnect();
    }

    public void testFTPServer() throws Exception {
        testFTPServerExplicitSSL(false);
        testFTPServerExplicitSSL(true);

        testFTPServerData(true, true, false);
        testFTPServerData(true, false, false);
        testFTPServerData(true, true, true);
        testFTPServerData(true, false, true);

        testFTPServerData(false, true, false);
        testFTPServerData(false, false, false);
        testFTPServerData(false, true, true);
        testFTPServerData(false, false, true);
    }

    @Test
    public void runServerTests() throws Exception {
        // No Proxy; client <-> simple server
        testSimpleServer();
        // client <-> proxy <-> simple server
        testProxyServer();
        // No Proxy; client <-> ftp server
        testFTPServer();
    }
}
