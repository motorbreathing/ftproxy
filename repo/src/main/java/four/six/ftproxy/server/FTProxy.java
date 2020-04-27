package four.six.ftproxy.server;

import four.six.ftproxy.ftp.FTPUtil;
import four.six.ftproxy.netty.NettyUtil;

public class FTProxy
{
    public static void main(String[] args) throws Exception
    {   
        new FTProxy().run();
    }

    private void runServer() throws Exception
    {
        FTPUtil.getFTProxyServerChannel().channel().closeFuture().sync();
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
