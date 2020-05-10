package four.six.ftproxy.netty;

import four.six.ftproxy.util.Util;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class DataRelayHandler extends ChannelInboundHandlerAdapter {

    protected ChannelHandlerContext clientCtx = null;
    protected ChannelHandlerContext serverCtx = null;

    protected void initContext(ChannelHandlerContext ctx) {
        if (clientCtx == null) {
            Util.log("DataRelayHandler: channel active (client)");
            clientCtx = ctx;
        } else {
            Util.log("DataRelayHandler: channel active (server)");
            serverCtx = ctx;
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        initContext(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (ctx == clientCtx) {
            Util.log("DataRelayHandler: removed (client)");
            clientCtx = null;
            if (serverCtx != null)
                serverCtx.close();
        } else if (ctx == serverCtx) {
            Util.log("DataRelayHandler: removed (server)");
            serverCtx = null;
            if (clientCtx != null)
                clientCtx.close();
        } else if (clientCtx != null && serverCtx != null) {
            throw new IllegalStateException("DataRelayHandler: unknown handler context removed");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Util.log("DataRelayHandler: channelRead");
        if (ctx == clientCtx) {
            // Unlikely scenario, but what do we know - we're just a simple
            // data relay handler and not prone to making assumptions about ftp
            // or any protocol for that matter.
            serverCtx.writeAndFlush(msg);
        } else if (ctx == serverCtx) {
            clientCtx.writeAndFlush(msg);
        } else if (clientCtx != null && serverCtx != null) {
            throw new IllegalStateException("DataRelayHandler: unknown context from read");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Util.log("DataRelayHandler: caught exception");
        cause.printStackTrace();
        closeSession();
    }

    public void closeSession() {
        if (clientCtx != null) {
            clientCtx.close();
        }
        if (serverCtx != null) {
            serverCtx.close();
        }
    }
}
