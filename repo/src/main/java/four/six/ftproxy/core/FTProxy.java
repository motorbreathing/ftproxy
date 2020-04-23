package four.six.ftproxy.core;

import io.netty.channel.ChannelOption;

import four.six.ftproxy.netty.ClientChannelInitializer;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.util.Util;

public class FTProxy
{
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
        .bind(Util.PORT).sync()
        .channel().closeFuture().sync();

    }

    public void run() throws Exception
    {
        try {
            runServer();
        } finally {
            NettyUtil.shutdown();
        }
    }
}
