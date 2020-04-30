package four.six.ftproxy.server;

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
    
    public boolean isRunning()
    {
        return running;
    }

    public void run()
    {
        try {
            Util.log("Attempting to start 'backend' server at port " + port);
            NettyUtil.getServerChannel(port, new TextRelayChannelInitializer() {
                @Override
                public ChannelHandler getProtocolHandler()
                {
                    return new TestEchoHandler();
                }
            }).sync();
            running = true;
        } catch (Exception e) {
            e.printStackTrace();
            running = false;
            return;
        }

        System.out.println("Echo server up and listening at " + port);

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            System.out.println("TestEchoServer: interrupted");
        }
    }
}
