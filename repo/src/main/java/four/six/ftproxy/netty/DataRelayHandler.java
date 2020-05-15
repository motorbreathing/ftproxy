package four.six.ftproxy.netty;

import four.six.ftproxy.util.Util;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicLong;

@Sharable
public class DataRelayHandler extends ChannelInboundHandlerAdapter {

    protected ChannelHandlerContext clientCtx = null;
    protected ChannelHandlerContext serverCtx = null;
    protected ChannelFuture lastClientWriteFuture = null;
    private AtomicLong pendingWrites = new AtomicLong(0);

    protected void initContext(ChannelHandlerContext ctx) {
        if (clientCtx == null) {
            Util.logFine("DataRelayHandler: channel active (client)");
            clientCtx = ctx;
        } else {
            Util.logFine("DataRelayHandler: channel active (server)");
            serverCtx = ctx;
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        initContext(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (ctx == serverCtx) {
            Util.logFine("DataRelayHandler: removed (server)");
            serverCtx = null;
            if (pendingWrites.get() == 0) {
                if (clientCtx != null)
                    clientCtx.close();
            } else {
                lastClientWriteFuture.addListener(
                        new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture f) {
                                Util.logFinest("DataRelayHandler: last client write done");
                                f.channel().close();
                            }
                        });
            }
        } else if (ctx == clientCtx) {
            Util.logFinest("DataRelayHandler: removed (client)");
            clientCtx = null;
            if (serverCtx != null)
                serverCtx.close();
        } else if (clientCtx != null && serverCtx != null) {
            throw new IllegalStateException("DataRelayHandler: unknown handler context removed");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Util.logFinest("DataRelayHandler: channelRead");
        if (ctx == clientCtx) {
            // Unlikely scenario, but what do we know - we're just a simple
            // data relay handler and not prone to making assumptions about ftp
            // or any protocol for that matter.
            serverCtx.writeAndFlush(msg);
        } else if (ctx == serverCtx) {
            pendingWrites.incrementAndGet();
            lastClientWriteFuture = clientCtx.writeAndFlush(msg);
            lastClientWriteFuture.addListener(
                    new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture f) {
                            pendingWrites.decrementAndGet();
                        }
                    });

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
        Util.logWarning("DataRelayHandler: caught exception");
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
