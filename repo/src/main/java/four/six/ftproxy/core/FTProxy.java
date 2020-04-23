package four.six.ftproxy.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.LineHandler;
import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.netty.ClientChannelInitializer;

public class FTProxy {

    static final String DEFAULT_PORT_STR = "8080";
    static final int PORT = Integer.parseInt(System.getProperty("port", DEFAULT_PORT_STR));
    static final int SERVER_BACKLOG = 128;

    public static void main(String[] args) throws Exception
    {   
        new FTProxy().run();
    }

    private void runServer() throws InterruptedException
    {
        NettyUtil.getServerBootstrap()
        .childHandler(new ClientChannelInitializer())
        .option(ChannelOption.SO_BACKLOG, SERVER_BACKLOG)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .bind(PORT).sync()
        .channel().closeFuture().sync();

    }

    public void run() throws Exception {
        try {
            runServer();
        } finally {
            NettyUtil.shutdown();
        }
    }
}
