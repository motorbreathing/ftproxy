package four.six.ftproxy.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;

import four.six.ftproxy.util.Util;

abstract class AbstractChannelInitializer extends ChannelInitializer<SocketChannel>
{
    abstract ChannelHandler getDecoder();
    abstract ChannelHandler getEncoder();
    abstract ChannelHandler getProtocolHandler();

    @Override
    public void initChannel(SocketChannel ch) throws Exception
    {
        ch.pipeline().addLast(getDecoder(),
                              getEncoder(),
                              getProtocolHandler());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception
    {
        Util.log("ChannelInitializer: caught exception");
        cause.printStackTrace();
        ctx.close();
    }
}
