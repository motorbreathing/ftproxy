package four.six.ftproxy.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.bootstrap.ServerBootstrap;

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
}
