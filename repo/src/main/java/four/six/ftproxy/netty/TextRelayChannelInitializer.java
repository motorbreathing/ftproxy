package four.six.ftproxy.netty;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.ssl.SSLHandlerProvider;

public class TextRelayChannelInitializer extends AbstractChannelInitializer
{
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    private boolean sslStatus;

    public TextRelayChannelInitializer()
    {
        init(false);
    }

    public TextRelayChannelInitializer(boolean sslStatus)
    {
        init(sslStatus);
    }

    private void init(boolean sslStatus)
    {
        this.sslStatus = sslStatus;
    }

    @Override
    public ChannelHandler getDecoder()
    {
        return DECODER;
    }

    @Override
    public ChannelHandler getEncoder()
    {
        return ENCODER;
    }

    @Override
    public ChannelHandler getProtocolHandler()
    {
        return new TextRelayHandler();
    }

    @Override
    public boolean SSLEnabled()
    {
        return sslStatus;
    }

    @Override
    public boolean isServer()
    {
        return true;
    }
}
