package four.six.ftproxy.ftp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.netty.NettyUtil;

public class FTPUtil
{
    public static final String UNRECOGNIZED_COMMAND_STR =
        "500 Syntax error - command unrecognized\r\n";

    public static ChannelFuture
        getFTProxyServerChannel() throws Exception
    {
        return getFTProxyServerChannel(false);
    }

    public static ChannelFuture
        getFTProxyServerChannel(boolean sslStatus) throws Exception
    {
        return NettyUtil.getServerChannel(new FTPChannelInitializer(sslStatus));
    }
}
