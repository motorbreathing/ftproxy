package four.six.ftproxy.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.channel.ChannelOption;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.TestClientHandler;
import four.six.ftproxy.util.Util;

public class TestClient {
    private ChannelInitializer<? extends Channel> getChannelInitializer()
    {
        return new ChannelInitializer<SocketChannel>() {
                       @Override
                       public void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline().addLast(new StringDecoder(),
                                                 new StringEncoder(),
                                                 new TestClientHandler());
                       }
                   };
    }

    public static void main(String[] args) throws Exception
    {
        new TestClient().connect();
    }

    public void doWork(Channel ch) throws Exception
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        ChannelFuture f = null;
        while (true)
        {
             String line = in.readLine();
             System.out.println("you: " + line);
             if (line == null)
                 break;
             f = ch.writeAndFlush(line + "\r\n");
             if (line.equals("bye"))
             {
                 ch.closeFuture().sync();
                 break;
             }

        }

        if (f != null)
            f.sync();
    }

    public void connect() throws Exception
    {
        try {
            ChannelFuture f = NettyUtil.getChannelToProxy(getChannelInitializer());
            f.sync();
            doWork(f.channel());
        } finally {
            NettyUtil.shutdown();
        }
    }
}
