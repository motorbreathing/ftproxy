package four.six.ftproxy.netty;


import four.six.ftproxy.util.Util;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

// Test comment; update to branch2; another update

abstract class AbstractChannelInitializer extends ChannelInitializer<SocketChannel> {
    // Child classes can provide all these, if so inclined
    abstract ChannelHandler getDecoder();

    abstract ChannelHandler getEncoder();

    abstract ChannelHandler getProtocolHandler();

    // Default: no SSL
    public ChannelHandler getSSLHandler(Channel ch) {
        return null;
    }

    // Installs the default read timeout
    public ChannelHandler getReadTimeoutHandler() {
        return new ReadTimeoutHandler(Util.READ_TIMEOUT_SECONDS);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelHandler sslHandler = getSSLHandler(ch);
        if (sslHandler != null)
            ch.pipeline().addFirst(sslHandler);

        ChannelHandler decoder = getDecoder();
        if (decoder != null)
            ch.pipeline().addLast(decoder);

        ChannelHandler encoder = getEncoder();
        if (encoder != null)
            ch.pipeline().addLast(encoder);

        ChannelHandler readTimeoutHandler = getReadTimeoutHandler();
        if (readTimeoutHandler != null)
            ch.pipeline().addLast(readTimeoutHandler);

        ChannelHandler protocolHandler = getProtocolHandler();
        if (protocolHandler != null)
            ch.pipeline().addLast(protocolHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Util.logWarning("AbstractChannelInitializer: caught exception");
        cause.printStackTrace();
        ctx.close();
    }
}
