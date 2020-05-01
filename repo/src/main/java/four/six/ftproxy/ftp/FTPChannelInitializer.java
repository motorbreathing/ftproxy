package four.six.ftproxy.ftp;

import io.netty.channel.ChannelHandler;

import four.six.ftproxy.netty.TextRelayChannelInitializer;

public class FTPChannelInitializer extends TextRelayChannelInitializer
{
    FTPRelayHandler handler = new FTPRelayHandler();

    public FTPChannelInitializer()
    {
        super(false);
    }

    public FTPChannelInitializer(boolean sslStatus)
    {
        super(sslStatus);
    }

    @Override
    public ChannelHandler getProtocolHandler()
    {
        return handler;
    }
}
