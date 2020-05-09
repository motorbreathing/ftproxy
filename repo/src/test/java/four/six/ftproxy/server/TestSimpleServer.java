package four.six.ftproxy.server;

import four.six.ftproxy.netty.TextRelayChannelInitializer;
import four.six.ftproxy.ssl.SSLHandlerProvider;
import four.six.ftproxy.util.Util;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

public class TestSimpleServer extends AbstractTestServer {
    private static final String myName = "Test Simple Server";
    private final TestSimpleHandler handler = createProtocolHandler();

    public TestSimpleServer() {
        setMyName(myName);
    }

    protected TestSimpleHandler createProtocolHandler() {
        return new TestSimpleHandler();
    }

    protected ChannelHandler getProtocolHandler() {
        return handler;
    }

    @Override
    protected ChannelInitializer<? extends Channel> getTestServerChannelInitializer() {
        return new TextRelayChannelInitializer() {
            @Override
            public ChannelHandler getSSLHandler(Channel ch) {
                if (sslStatus) return SSLHandlerProvider.getServerSSLHandler(ch);
                else return null;
            }

            @Override
            public ChannelHandler getProtocolHandler() {
                return TestSimpleServer.this.getProtocolHandler();
            }
        };
    }

    void enableExplicitSSL() {
        if (handler == null) {
            Util.log("Warning: TextSimpleServer: handling missing, can't enable SSL");
            return;
        }
        handler.enableExplicitSSL();
    }
}
