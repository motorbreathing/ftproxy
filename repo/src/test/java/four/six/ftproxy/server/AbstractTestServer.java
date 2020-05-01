package four.six.ftproxy.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;

abstract class AbstractTestServer extends Thread {
    private int port = 6666;
    private volatile boolean running = false;
    private String name = "Test Server";

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

    public void setMyName(String name)
    {
        this.name = name;
    }

    protected abstract
        ChannelInitializer<? extends Channel> getTestServerChannelInitializer();

    public void run()
    {
        Channel ch = null;
        try {
            Util.log(name + ": attempting to start at port " + port);
            ch = NettyUtil.getServerChannel(port, getTestServerChannelInitializer())
                     .sync().channel();
            running = true;
            Util.log(name + ": up and running at port " + port);
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            Util.log(name + " at port " + port + ": interrupted; shutting down");
            ch.close();
        } catch (Exception e) {
            Util.log(name + ": failed to bind at port " + port);
            e.printStackTrace();
        }
        running = false;
        return;
    }
}
