package four.six.ftproxy.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;

import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.util.Util;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel>
{
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    private ChannelHandler lineHandler;

    public ServerChannelInitializer(ChannelHandler lh)
    {
        lineHandler = lh;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception
    {
        ch.pipeline().addLast(DECODER,
                              ENCODER,
                              lineHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception
    {
        Util.log("ServerChannelInitializer: caught exception");
        cause.printStackTrace();
        ctx.close();
    }
}
