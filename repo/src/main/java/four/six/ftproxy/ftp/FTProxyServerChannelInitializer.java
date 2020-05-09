package four.six.ftproxy.ftp;

import four.six.ftproxy.netty.TextRelayChannelInitializer;
import four.six.ftproxy.ssl.SSLHandlerProvider;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

public class FTProxyServerChannelInitializer extends TextRelayChannelInitializer {
    FTPRelayHandler handler;
    boolean sslEnabled;

    public FTProxyServerChannelInitializer() {
        sslEnabled = false;
        handler = null;
    }

    public FTProxyServerChannelInitializer(FTPRelayHandler handler, boolean sslEnabled) {
        this.handler = handler;
        this.sslEnabled = sslEnabled;
    }

    public FTProxyServerChannelInitializer(FTPRelayHandler handler) {
        this.handler = handler;
        this.sslEnabled = false;
    }

    public FTProxyServerChannelInitializer(boolean sslEnabled) {
        this.handler = null;
        this.sslEnabled = sslEnabled;
    }

    @Override
    public ChannelHandler getProtocolHandler() {
        if (handler == null)
            return new FTPRelayHandler();
        return handler;
    }

    @Override
    public ChannelHandler getSSLHandler(Channel ch) {
        if (sslEnabled)
            return SSLHandlerProvider.getServerSSLHandler(ch);

        return null;
    }
}
