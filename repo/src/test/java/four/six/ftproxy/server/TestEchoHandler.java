package four.six.ftproxy.server;

import four.six.ftproxy.ssl.SSLHandlerProvider;
import four.six.ftproxy.util.Util;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestEchoHandler extends SimpleChannelInboundHandler<String> {

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Util.log("TestEchoHandler: channel active");
        this.ctx = ctx;
    }

    protected void process(ChannelHandlerContext ctx, String incoming) {
        if (incoming.equalsIgnoreCase("quit")) ctx.close();
        else ctx.writeAndFlush(incoming);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception {
        Util.log("TestEchoHandler: read: " + incoming);
        process(ctx, incoming);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void enableExplicitSSL() {
        ctx.pipeline().addFirst(SSLHandlerProvider.getServerSSLHandler(ctx.channel()));
    }
}
