package four.six.ftproxy.ftp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.ftp.FTPChannelInitializer;
import four.six.ftproxy.netty.NettyUtil;

public class FTPUtil
{
    public static ChannelInitializer getFTPChannelInitializer()
    {
        return new FTPChannelInitializer();
    }

    public static ChannelFuture
        getFTProxyServerChannel() throws Exception
    {
        return NettyUtil.getServerChannel(getFTPChannelInitializer());
    }
}
