package four.six.ftproxy.netty;

import four.six.ftproxy.util.Util;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class DataRelayHandler extends ChannelInboundHandlerAdapter {

    protected ChannelHandlerContext oneCtx = null;
    protected ChannelHandlerContext otherCtx = null;

    protected void initContext(ChannelHandlerContext ctx) {
        if (oneCtx == null) {
            Util.log("DataRelayHandler: channel active (one)");
            oneCtx = ctx;
        } else {
            Util.log("DataRelayHandler: channel active (other)");
            otherCtx = ctx;
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        initContext(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (ctx == oneCtx) {
            Util.log("DataRelayHandler: removed (one)");
            oneCtx = null;
            if (otherCtx != null) otherCtx.close();
        } else if (ctx == otherCtx) {
            Util.log("DataRelayHandler: removed (other)");
            otherCtx = null;
            if (oneCtx != null) oneCtx.close();
        } else {
            throw new IllegalStateException("DataRelayHandler: unknown handler context removed");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == oneCtx) otherCtx.writeAndFlush(msg);
        else if (ctx == otherCtx) oneCtx.writeAndFlush(msg);
        else throw new IllegalStateException("DataRelayHandler: unknown context from read");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Util.log("DataRelayHandler: caught exception");
        cause.printStackTrace();
        ctx.close();
    }

    public void closeSession() {
        if (oneCtx != null) {
            oneCtx.close();
        }
        if (otherCtx != null) {
            otherCtx.close();
        }
    }
}
