package four.six.ftproxy.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;

import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.netty.TextRelayHandler;
import four.six.ftproxy.util.Util;

public class TextRelayChannelInitializer extends AbstractChannelInitializer
{
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    public ChannelHandler getDecoder()
    {
        return DECODER;
    }

    public ChannelHandler getEncoder()
    {
        return ENCODER;
    }

    public ChannelHandler getProtocolHandler()
    {
        return new TextRelayHandler();
    }
}
