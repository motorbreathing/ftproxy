package four.six.ftproxy.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;

import four.six.ftproxy.util.Util;

public class NettyUtil 
{
    public static EventLoopGroup bossGroup = new NioEventLoopGroup();
    public static EventLoopGroup workerGroup = new NioEventLoopGroup();


    public static void shutdown()
    {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static ServerBootstrap getServerBootstrap()
    {
        return new ServerBootstrap()
                   .group(bossGroup, workerGroup)
                   .channel(NioServerSocketChannel.class);
    }

    public static Bootstrap getClientBootstrap()
    {
        return new Bootstrap()
                   .group(workerGroup)
                   .channel(NioSocketChannel.class);
    }

    public ChannelFuture getChannelToRemoteHost() throws Exception
    {
         Bootstrap b = getClientBootstrap();
         b.option(ChannelOption.SO_KEEPALIVE, true);
         return b.connect(Util.REMOTE_HOST, Util.REMOTE_PORT);
    }

    public static ChannelFuture
        getChannelToProxy(ChannelInitializer<? extends Channel> ci) throws Exception
    {
        Bootstrap b = getClientBootstrap();
        b.option(ChannelOption.SO_KEEPALIVE, true);
        if (ci != null)
            b.handler(ci);
        return b.connect(Util.HOST, Util.PORT);
    }
}
