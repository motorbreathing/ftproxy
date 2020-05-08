package four.six.ftproxy.server;


public class TestFTPServer extends TestEchoServer {
    private static final String myName = "Test FTP Server";

    public TestFTPServer() {
        setMyName(myName);
    }

    protected TestEchoHandler createProtocolHandler() {
        return new TestFTPHandler();
    }
}
