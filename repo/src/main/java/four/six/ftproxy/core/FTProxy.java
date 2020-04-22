package four.six.ftproxy.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import four.six.ftproxy.netty.LineHandler;
import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.StringEncoder;

public class FTProxy {

    static final String DEFAULT_PORT_STR = "8080";
    static final int PORT = Integer.parseInt(System.getProperty("port", DEFAULT_PORT_STR));
    static final int SERVER_BACKLOG = 128;

    public static void main(String[] args) throws Exception
    {   
        new FTProxy().run();
    }

    private void setupChannelInitializer(ServerBootstrap b)
    {
        ChannelInitializer<SocketChannel> myChan =
        new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception
            {
                ch.pipeline().addLast(new StringDecoder(),
                                      new StringEncoder(),
                                      new LineHandler());
            }
        };

        b.childHandler(myChan);
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            setupChannelInitializer(b);
            b.option(ChannelOption.SO_BACKLOG, SERVER_BACKLOG);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.bind(PORT).sync()
             .channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
