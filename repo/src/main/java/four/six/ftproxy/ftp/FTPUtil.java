package four.six.ftproxy.ftp;

import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.ftp.FTPChannelInitializer;

public class FTPUtil
{
    public static ChannelInitializer getFTPChannelInitializer()
    {
        return new FTPChannelInitializer();
    }
}
