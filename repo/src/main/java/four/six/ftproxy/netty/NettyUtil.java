package four.six.ftproxy.netty;

import four.six.ftproxy.util.Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class NettyUtil {
    public static EventLoopGroup bossGroup = new NioEventLoopGroup();
    public static EventLoopGroup workerGroup = new NioEventLoopGroup();

    public static void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static ServerBootstrap getServerBootstrap() {
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class);
    }

    public static Bootstrap getClientBootstrap() {
        return new Bootstrap().group(workerGroup).channel(NioSocketChannel.class);
    }

    public static ChannelFuture getChannelToRemoteHost(ChannelInitializer<? extends Channel> ci) {
        return getChannelToHost(Util.getRemoteHost(), Util.getRemotePort(), ci);
    }

    public static ChannelFuture getChannelToHost(
            String host, int port, ChannelInitializer<? extends Channel> ci) {
        Bootstrap b = getClientBootstrap();
        b.option(ChannelOption.SO_KEEPALIVE, true);
        if (ci != null)
            b.handler(ci);
        return b.connect(host, port);
    }

    public static ChannelFuture getChannelToAddress(
            InetSocketAddress address, ChannelInitializer<? extends Channel> ci) {
        Bootstrap b = getClientBootstrap();
        b.option(ChannelOption.SO_KEEPALIVE, true);
        if (ci != null)
            b.handler(ci);
        return b.connect(address);
    }

    public static ChannelFuture getServerChannel(ChannelInitializer<? extends Channel> ci)
            throws Exception {
        return getServerChannel(Util.getServerHost(), Util.getServerPort(), ci);
    }

    public static ChannelFuture getServerChannel(int port, ChannelInitializer<? extends Channel> ci)
            throws Exception {
        ServerBootstrap b = getServerBootstrap();
        if (ci != null)
            b.childHandler(ci);
        b.option(ChannelOption.SO_BACKLOG, Util.getServerBacklog());
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        return b.bind(port);
    }

    public static ChannelFuture getServerChannel(
            String host, int port, ChannelInitializer<? extends Channel> ci) throws Exception {
        ServerBootstrap b = getServerBootstrap();
        if (ci != null)
            b.childHandler(ci);
        b.option(ChannelOption.SO_BACKLOG, Util.getServerBacklog());
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        return b.bind(host, port);
    }

    public static ChannelFuture getListenerChannel(
            InetAddress addr, ChannelInitializer<? extends Channel> ci) {
        ServerBootstrap b = getServerBootstrap();
        if (ci != null)
            b.childHandler(ci);
        b.option(ChannelOption.SO_BACKLOG, Util.getServerBacklog());
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        for (int p = Util.dataPortMin; p <= Util.dataPortMax; p++) {
            try {
                // XXX : should be async?
                return b.bind(addr, p).sync();
            } catch (Exception e) {
                Util.log("Listener bind: port " + p + " not available, retrying next");
                continue;
            }
        }
        Util.log(
                "Listener: failed to bind (in range " + Util.dataPortMin + "-" + Util.dataPortMax + ")");
        return null;
    }
}
