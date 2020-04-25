package four.six.ftproxy.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;

import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.netty.LineHandler;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel>
{
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    private ChannelHandler last;

    public ServerChannelInitializer(ChannelHandler l)
    {
        last = l;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception
    {
        ch.pipeline().addLast(DECODER,
                              ENCODER,
                              last); //new LineHandler(true));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception
    {
        System.out.println("ServerChannelInitializer: caught exception!");
        cause.printStackTrace();
        ctx.close();
    }
}
