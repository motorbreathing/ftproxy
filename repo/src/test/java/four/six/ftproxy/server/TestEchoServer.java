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
    private final TestEchoHandler handler = new TestEchoHandler();

    public TestEchoServer()
    {
        setMyName(myName);
    }

    @Override
    protected ChannelInitializer<? extends Channel> getTestServerChannelInitializer()
    {
        if (sslStatus)
            return new TextRelayChannelInitializer() {
                        @Override
                        public ChannelHandler getSSLHandler(Channel ch)
                        {
                            return SSLHandlerProvider.getServerSSLHandler(ch);
                        }

                        @Override
                        public ChannelHandler getProtocolHandler()
                        {
                            return handler;
                        }
                   };
        else
            return new TextRelayChannelInitializer() {
                        @Override
                        public ChannelHandler getProtocolHandler()
                        {
                            return handler;
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
