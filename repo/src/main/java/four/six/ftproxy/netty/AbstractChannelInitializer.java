package four.six.ftproxy.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.ssl.SSLHandlerProvider;

abstract class AbstractChannelInitializer extends ChannelInitializer<SocketChannel>
{
    abstract ChannelHandler getDecoder();
    abstract ChannelHandler getEncoder();
    abstract ChannelHandler getProtocolHandler();

    abstract boolean SSLEnabled();
    abstract boolean isServer();

    public SslHandler getSSLHandler(Channel ch)
    {
        if (!SSLEnabled())
            return null;

        return isServer() ? SSLHandlerProvider.getServerSSLHandler(ch)
                          : SSLHandlerProvider.getClientSSLHandler(ch);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception
    {
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
