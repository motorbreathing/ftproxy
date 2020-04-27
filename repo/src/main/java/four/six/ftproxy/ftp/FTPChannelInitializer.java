package four.six.ftproxy.ftp;

import io.netty.channel.ChannelHandler;

import four.six.ftproxy.ftp.FTPRelayHandler;
import four.six.ftproxy.netty.TextRelayChannelInitializer;

public class FTPChannelInitializer extends TextRelayChannelInitializer
{
    @Override
    public ChannelHandler getProtocolHandler()
    {
        return new FTPRelayHandler();
    }
}
