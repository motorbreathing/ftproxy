package four.six.ftproxy.netty;

import four.six.ftproxy.util.Util;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Util.log("TestClientHandler: channelActive");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception {
        Util.log("TestClientHandler: channelRead: " + incoming);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Util.log("TestClientHandler: caught exception:\n +" + cause.toString());
        ctx.close();
    }
}
