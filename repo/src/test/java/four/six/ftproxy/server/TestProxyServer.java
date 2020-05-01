package four.six.ftproxy.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.ftp.FTPChannelInitializer;

public class TestProxyServer extends TestServer {

    private final static String myName = "Test Proxy Server";
    public TestProxyServer()
    {
        setMyName(myName);
    }

    @Override
    protected ChannelInitializer<? extends Channel> getTestServerChannelInitializer()
    {
        return new FTPChannelInitializer();
    }
}
