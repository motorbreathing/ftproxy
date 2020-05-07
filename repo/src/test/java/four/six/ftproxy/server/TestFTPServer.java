package four.six.ftproxy.server;

import io.netty.channel.ChannelHandler;

public class TestFTPServer extends TestEchoServer {
    private final static String myName = "Test FTP Server";

    public TestFTPServer()
    {
        setMyName(myName);
    }

    protected TestEchoHandler createProtocolHandler()
    {
        return new TestFTPHandler();
    }
}
