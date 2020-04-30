package four.six.ftproxy.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.TextRelayChannelInitializer;

public class TestEchoServer extends Thread {
    private int port = 7070;
    private volatile boolean running = false;

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getPort()
    {
        return port;
    }
    
    public boolean isRunning()
    {
        return running;
    }

    public void run()
    {
        try {
            Util.log("Attempting to start 'backend' server at port " + port);
            ChannelFuture f = 
            NettyUtil.getServerChannel(port, new TextRelayChannelInitializer() {
                @Override
                public ChannelHandler getProtocolHandler()
                {
                    return new TestEchoHandler();
                }
            }).sync().channel().closeFuture();
            running = true;
            System.out.println("Echo server up and listening at " + port);
            f.sync();
        } catch (Exception e) {
            e.printStackTrace();
            running = false;
            return;
        }
    }
}
