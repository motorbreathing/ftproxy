package four.six.ftproxy.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import four.six.ftproxy.netty.TestClientHandler;
import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.StringEncoder;

public class TestClient {
    static final String DEFAULT_HOST_STR = "127.0.0.1";
    static final String DEFAULT_PORT_STR = "8080";
    static final String HOST = System.getProperty("host", DEFAULT_HOST_STR);
    static final int PORT = Integer.parseInt(System.getProperty("port", DEFAULT_PORT_STR));

    private void setupChannelInitializer(Bootstrap b)
    {
        ChannelInitializer<SocketChannel> myChan =
            new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                  throws Exception {
                    ch.pipeline().addLast(new StringDecoder(),
                                          new StringEncoder(),
                                          new TestClientHandler());
                }
            };
        b.handler(myChan);
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
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            setupChannelInitializer(b);
            ChannelFuture f = b.connect(HOST, PORT).sync();
            doWork(f.channel());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
