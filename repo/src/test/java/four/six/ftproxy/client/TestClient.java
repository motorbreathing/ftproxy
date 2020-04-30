package four.six.ftproxy.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.TestClientHandler;
import four.six.ftproxy.util.Util;
import four.six.ftproxy.util.LineProvider;

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

    private ChannelInitializer<? extends Channel> getChannelInitializer()
    {
        return new ChannelInitializer<SocketChannel>() {
                       @Override
                       public void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline().addLast(new StringDecoder(),
                                                 new StringEncoder(),
                                                 new TestClientHandler() {
                                                     @Override
                                                     public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception
                                                     {
                                                         addString(incoming);
                                                     }
                                                 });
                       }
                   };
    }

    public void write(String line) throws Exception
    {
         ch.writeAndFlush(line).sync();
    }

    public void connect(String host, int port) throws Exception
    {
        try {
            ch = NettyUtil.getChannelToHost(host, port, getChannelInitializer()).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void disconnect() throws Exception
    {
        ch.close();
    }
}
