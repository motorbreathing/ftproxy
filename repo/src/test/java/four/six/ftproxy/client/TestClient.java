package four.six.ftproxy.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;

import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.TestClientHandler;
import four.six.ftproxy.util.Util;
import four.six.ftproxy.util.LineProvider;
import four.six.ftproxy.ssl.SSLHandlerProvider;

public class TestClient {
    private Channel ch;
    private LineProvider lp;
    private Lock lpLock;
    private Condition lpCond;

    public TestClient()
    {
        lp = new LineProvider();
        lpLock = new ReentrantLock();
        lpCond = lpLock.newCondition();
    }

    public void addString(String data)
    {
        lpLock.lock();
        lp.add(data);
        lpCond.signalAll();
        lpLock.unlock();
    }

    public String readLine(long millis)
    {
        try {
            lpLock.lock();
            String res = lp.getLine();
            if (res != null)
                return res;
            lpCond.await(millis, TimeUnit.MILLISECONDS);
            return lp.getLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lpLock.unlock();
        }
        return null;
    }

    private ChannelInitializer<? extends Channel> getChannelInitializer(boolean ssl)
    {
        TestClientHandler tch =
            new TestClientHandler() {
                    @Override
                    public void channelRead0(ChannelHandlerContext ctx,
                                             String incoming) throws Exception
                    {
                        addString(incoming);
                    }
            };

        if (ssl) // SSL enabled
        return new ChannelInitializer<SocketChannel>() {
                       @Override
                       public void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline().addLast(SSLHandlerProvider.getClientSSLHandler(ch),
                                                 new StringDecoder(),
                                                 new StringEncoder(),
                                                 tch);
                       }
                   };
        else // SSL disabled
        return new ChannelInitializer<SocketChannel>() {
                       @Override
                       public void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline().addLast(new StringDecoder(),
                                                 new StringEncoder(),
                                                 tch);
                       }
                   };
    }

    public void write(String line) throws Exception
    {
         ch.writeAndFlush(line).sync();
    }

    public void connect(String host, int port, boolean ssl) throws Exception
    {
        try {
            Util.log("Client: attempting to connect to " + host + " at port " + port);
            ch = NettyUtil.getChannelToHost(host, port, getChannelInitializer(ssl))
                          .sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void connect(String host, int port) throws Exception
    {
        connect(host, port, false);
    }

    public void disconnect() throws Exception
    {
        Util.log("Client: disconnecting");
        ch.close();
    }

    public void enableSSL()
    {
        if (ch != null)
            ch.pipeline()
              .addFirst(SSLHandlerProvider.getClientSSLHandler(ch));
        else
            Util.log("Warning: enableSSL() failed for client");
    }
}
