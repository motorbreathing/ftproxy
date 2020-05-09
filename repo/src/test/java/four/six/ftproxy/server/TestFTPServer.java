package four.six.ftproxy.server;


public class TestFTPServer extends TestSimpleServer {
    private static final String myName = "Test FTP Server";

    public TestFTPServer() {
        setMyName(myName);
    }

    protected TestSimpleHandler createProtocolHandler() {
        return new TestFTPHandler();
    }
}
