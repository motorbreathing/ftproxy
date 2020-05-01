package four.six.ftproxy.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.TextRelayChannelInitializer;

public class TestEchoServer extends AbstractTestServer {
    private final static String myName = "Test Echo Server";

    public TestEchoServer()
    {
        setMyName(myName);
    }

    @Override
    protected ChannelInitializer<? extends Channel> getTestServerChannelInitializer()
    {
        return new TextRelayChannelInitializer() {
                       @Override
                       public ChannelHandler getProtocolHandler()
                       {
                           return new TestEchoHandler();
                       }
                    };

    }
}
