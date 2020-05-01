package four.six.ftproxy.server;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.ssl.SSLHandlerProvider;

abstract class AbstractTestServer extends Thread {
    private int port = 6666;
    private volatile boolean running = false;
    protected boolean sslStatus = false;
    protected String myName = "Test Server";
    protected Channel ch;

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
        this.myName = name;
    }

    protected abstract
        ChannelInitializer<? extends Channel> getTestServerChannelInitializer();

    public void enableSSL()
    {
        if (running)
            Util.log("Warning: server is already running; enableSSL() is no-op");

        sslStatus = true;
    }

    public boolean SSLEnabled()
    {
        return sslStatus;
    }

    public void run()
    {
        try {
            Util.log(myName + ": attempting to start at port " + port);
            ch = NettyUtil.getServerChannel(port, getTestServerChannelInitializer())
                     .sync().channel();
            running = true;
            Util.log(myName + ": up and running at port " + port);
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            Util.log(myName + " at port " + port + ": interrupted; shutting down");
            ch.close();
        } catch (Exception e) {
            Util.log(myName + ": failed to bind at port " + port);
            e.printStackTrace();
        }
        running = false;
        return;
    }
}
