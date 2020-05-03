package four.six.ftproxy.netty;

import io.netty.channel.ChannelHandler;

public class DataRelayChannelInitializer extends AbstractChannelInitializer
{
    @Override
    public ChannelHandler getDecoder()
    {
        return null;
    }

    @Override
    public ChannelHandler getEncoder()
    {
        return null;
    }

    @Override
    public ChannelHandler getProtocolHandler()
    {
        return null;
    }
}
