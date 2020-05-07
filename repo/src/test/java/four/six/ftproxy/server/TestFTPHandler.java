package four.six.ftproxy.server;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.net.InetSocketAddress;

import four.six.ftproxy.ssl.SSLHandlerProvider;
import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.ftp.FTPUtil;
import four.six.ftproxy.ftp.FTPAuthCommand;
import four.six.ftproxy.ftp.FTPProtCommand;
import four.six.ftproxy.ftp.FTPPortCommand;

public class TestFTPHandler extends TestEchoHandler {

    public static final String GENERIC_RESPONSE_200_STR = "200 Generic Approval\r\n";
    public static final String PROT_RESPONSE_200_C_STR = "200 Protection level set to C\r\n";
    public static final String PROT_RESPONSE_200_P_STR = "200 Protection level set to P\r\n";
    public static final String JACKAL_STR = "The Day Of The Jackal";
    private static final Charset charset = Charset.forName(Util.UTF8_STR);

    private ChannelHandlerContext ctx;
    boolean sslAllowed = true;
    boolean dataSSLEnabled = false;

    public void unimplementSSL()
    {
        sslAllowed = false;
    }

    protected ChannelHandler getFileSendChannelHandler()
    {
        return new ChannelInboundHandlerAdapter() {
                       @Override
                       public void channelActive(ChannelHandlerContext ctx)
                           throws Exception
                       {
                           Util.log("TestFTPServer: Active mode: connected");
                           ByteBuf response =
                               Unpooled.copiedBuffer(JACKAL_STR, charset);
                           ChannelFuture cf = ctx.writeAndFlush(response);
                           cf.addListener(new ChannelFutureListener() {
                               @Override
                               public void operationComplete(ChannelFuture f) {
                                   f.channel().close();
                               }
                           });
                        }
                     };
    }

    protected ChannelInitializer<SocketChannel> getFileSendChannelInitializer()
    {
        ChannelInitializer<SocketChannel> ci =
            new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                Util.log("File send: initializing channel");
                if (dataSSLEnabled) {
                    Util.log("File send: enabling SSL");
                    ch.pipeline().addFirst(SSLHandlerProvider.getServerSSLHandler(ch));
                }
                ChannelHandler handler = getFileSendChannelHandler();
                ch.pipeline().addLast(handler);
            }
        };
        return ci;
    }

    protected void process(ChannelHandlerContext ctx, String incoming) {
        String[] args = incoming.split(Util.REGEX_SPLIT_BY_SPACES);
        if (args[0].equalsIgnoreCase(FTPAuthCommand.COMMAND_STR)) {
            if (args[1].equalsIgnoreCase("SSL") || args[1].equalsIgnoreCase("TLS")) {
                if (sslAllowed) {
                    ctx.writeAndFlush(FTPAuthCommand.RESPONSE_STR);
                    enableExplicitSSL();
                } else {
                    ctx.writeAndFlush(FTPUtil.UNIMPLEMENTED_COMMAND_STR);
                }

            } else {
                ctx.writeAndFlush(FTPUtil.UNRECOGNIZED_COMMAND_STR);
            }
            return;
        }

        if (args[0].equalsIgnoreCase(FTPProtCommand.COMMAND_STR)) {
            if (args[1].equalsIgnoreCase("P")) {
                ctx.writeAndFlush(PROT_RESPONSE_200_P_STR);
                dataSSLEnabled = true;
            } else {
                ctx.writeAndFlush(PROT_RESPONSE_200_C_STR);
                dataSSLEnabled = false;
            }
            return;
        }

        if (args[0].equalsIgnoreCase(FTPPortCommand.COMMAND_STR)) {
            InetSocketAddress address = FTPUtil.processCommaDelimitedV4SocketAddress(args[1]);
            ChannelInitializer<SocketChannel> ci = getFileSendChannelInitializer();
            NettyUtil.getChannelToAddress(address, ci);
            return;
        }
        ctx.writeAndFlush(GENERIC_RESPONSE_200_STR);
    }
}
