package four.six.ftproxy.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.ssl.SSLHandlerProvider;
import four.six.ftproxy.netty.TextRelayChannelInitializer;

public class TestEchoServer extends AbstractTestServer {
    private final static String myName = "Test Echo Server";
    private final TestEchoHandler handler = createProtocolHandler();

    public TestEchoServer()
    {
        setMyName(myName);
    }

    protected TestEchoHandler createProtocolHandler()
    {
        return new TestEchoHandler();
    }

    protected ChannelHandler getProtocolHandler()
    {
        return handler;
    }

    @Override
    protected ChannelInitializer<? extends Channel> getTestServerChannelInitializer()
    {
        return new TextRelayChannelInitializer() {
                       @Override
                       public ChannelHandler getSSLHandler(Channel ch)
                       {
                           if (sslStatus)
                               return SSLHandlerProvider.getServerSSLHandler(ch);
                           else
                               return null;
                       }

                       @Override
                       public ChannelHandler getProtocolHandler()
                       {
                           return TestEchoServer.this.getProtocolHandler();
                       }
                   };
    }

    void enableExplicitSSL()
    {
        if (handler == null)
        {
            Util.log("Warning: TextEchoServer: handling missing, can't enable SSL");
            return;
        }
        handler.enableExplicitSSL();
    }
}
