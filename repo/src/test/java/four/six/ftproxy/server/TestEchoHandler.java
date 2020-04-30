package four.six.ftproxy.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestEchoHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        if (incoming.equalsIgnoreCase("iquit"))
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

