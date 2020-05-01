package four.six.ftproxy.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import four.six.ftproxy.util.Util;

abstract class AbstractChannelInitializer extends ChannelInitializer<SocketChannel>
{
    abstract ChannelHandler getDecoder();
    abstract ChannelHandler getEncoder();
    abstract ChannelHandler getProtocolHandler();
    abstract boolean SSLEnabled();
    abstract SslHandler getSSLHandler(Channel ch);

    @Override
    public void initChannel(SocketChannel ch) throws Exception
    {
        if (SSLEnabled())
            System.out.println("SSL is enabled in initializer");

        if (SSLEnabled())
            ch.pipeline().addLast(getSSLHandler(ch),
                                  getDecoder(),
                                  getEncoder(),
                                  getProtocolHandler());
        else
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
