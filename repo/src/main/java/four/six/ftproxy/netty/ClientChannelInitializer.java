package four.six.ftproxy.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;

import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.netty.LineHandler;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel>
{
    @Override
    public void initChannel(SocketChannel ch) throws Exception
    {
        ch.pipeline().addLast(new StringDecoder(),
                              new StringEncoder(),
                              new LineHandler());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        System.out.println("Caught exception!");
    }
}
