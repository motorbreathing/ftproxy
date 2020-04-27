package four.six.ftproxy.ftp;

import io.netty.channel.ChannelHandler;

import four.six.ftproxy.netty.TextRelayHandler;

public class FTPRelayHandler extends TextRelayHandler
{
    @Override
    public ChannelHandler getChannelHandler()
    {
        return this;
    }

    @Override
    public String process(String line)
    {
        return line;
    }
}
