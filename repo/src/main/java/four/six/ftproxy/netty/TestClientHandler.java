package four.six.ftproxy.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx)
      throws Exception {
        System.out.println("TestClientHandler: channelActive");
        ctx.writeAndFlush("Hello there, FTP Server!\n");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        System.out.println("TestClientHandler: channelRead");
        System.out.println(incoming);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}