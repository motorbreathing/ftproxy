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
import four.six.ftproxy.ftp.FTPUtil;

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

    public static ChannelFuture
        getChannelToRemoteHost(ChannelInitializer<? extends Channel> ci)
    {
        return getChannelToHost(Util.REMOTE_HOST, Util.REMOTE_PORT, ci);
    }

    public static ChannelFuture
        getChannelToProxy(ChannelInitializer<? extends Channel> ci)
    {
        return getChannelToHost(Util.THIS_HOST, Util.THIS_PORT, ci);
    }

    public static ChannelFuture
        getChannelToHost(String host, int port,
                         ChannelInitializer<? extends Channel> ci)
    {
        Bootstrap b = getClientBootstrap();
        b.option(ChannelOption.SO_KEEPALIVE, true);
        if (ci != null)
            b.handler(ci);
        return b.connect(host, port);
    }

    public static ChannelFuture
        getServerChannel(ChannelInitializer<? extends Channel> ci) throws Exception
    {
        ServerBootstrap b = getServerBootstrap();
        if (ci != null)
            b.childHandler(ci);
        b.option(ChannelOption.SO_BACKLOG, Util.SERVER_BACKLOG);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        return b.bind(Util.THIS_PORT).sync();
    }
}
