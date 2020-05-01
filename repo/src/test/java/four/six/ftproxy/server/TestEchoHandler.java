package four.six.ftproxy.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import four.six.ftproxy.ssl.SSLHandlerProvider;

public class TestEchoHandler extends SimpleChannelInboundHandler<String> {

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        System.out.println("TestEchoHandler: channel active");
        this.ctx = ctx;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        System.out.println("TestEchoHandler: read: " + incoming);
        if (incoming.equalsIgnoreCase("quit"))
            ctx.close();
        else
            ctx.writeAndFlush(incoming);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }
}

