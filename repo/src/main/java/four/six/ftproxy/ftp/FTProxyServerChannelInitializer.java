package four.six.ftproxy.ftp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import four.six.ftproxy.ssl.SSLHandlerProvider;
import four.six.ftproxy.netty.TextRelayChannelInitializer;

public class FTProxyServerChannelInitializer extends TextRelayChannelInitializer
{
    FTPRelayHandler handler = new FTPRelayHandler();
    boolean sslEnabled;

    public FTProxyServerChannelInitializer()
    {
        sslEnabled = false;
    }

    public FTProxyServerChannelInitializer(boolean sslEnabled)
    {
        this.sslEnabled = sslEnabled;
    }

    @Override
    public ChannelHandler getProtocolHandler()
    {
        return handler;
    }

    @Override
    public ChannelHandler getSSLHandler(Channel ch)
    {
        if (sslEnabled)
            return SSLHandlerProvider.getServerSSLHandler(ch);

        return null;
    }
}
