package four.six.ftproxy.netty;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.util.LineProvider;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.ServerChannelInitializer;

@Sharable
public class LineHandler extends SimpleChannelInboundHandler<String> {

    private LineProvider lp;
    private boolean isServer;
    private LineHandler other;
    private ChannelHandlerContext channelHandlerCtx;
    private boolean serverReady;

    public LineHandler()
    {
        init(false, null);
    }

    public LineHandler(boolean serverflag, LineHandler o)
    {
        init(serverflag, o);
    }

    private void init(boolean serverflag, LineHandler o)
    {
        lp = new LineProvider();
        other = o;
        isServer = serverflag;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelHandlerCtx = ctx;
        if (isServer == false)
        {
            Util.log("Client channel active!");
            ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
            ctx.write("It is " + new Date() + " now.\r\n");
            ctx.flush();
            other = new LineHandler(true, this);
            ChannelFuture cf =
                NettyUtil.getChannelToRemoteHost(new ServerChannelInitializer(other));
            cf.addListener(new ChannelFutureListener() {
                               public void operationComplete(ChannelFuture f)
                               {
                                   if (f.isSuccess())
                                   {
                                       Util.log("Server channel connected");
                                   } else {
                                       Util.log("Server channel failed to connect");
                                       channelHandlerCtx.writeAndFlush("FTP server unavailable\r\n");
                                       channelHandlerCtx.close();
                                   }
                               }
                           });
        } else {
            serverReady = true;
            Util.log("Backend server channel active!");
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
    {
        Util.log("LineHandler: removed");
        if (other != null)
            other.close();
    }

	public void clientRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        lp.add(incoming);
        if (other == null)
        {
            Util.log("Can't find server side context; stashing read");
            return;
        }
        while (true)
        {
            String line = lp.getLine();
            if (line == null)
                break;
            other.write(line);
            Util.log("[from client] " + line);
        }
    }

	public void serverRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        lp.add(incoming);
        while (true)
        {
            String line = lp.getLine();
            if (line == null)
                break;
            other.write(line);
            Util.log("[from server] " + line);
        }
    }

    private void clientWrite(String incoming)
    {
        channelHandlerCtx.write(incoming);
        channelHandlerCtx.flush();
    }

    private void serverWrite(String incoming)
    {
        if (!serverReady)
        {
            Util.log("Discarding writes: server not ready");
        } else {
            channelHandlerCtx.write(incoming);
            channelHandlerCtx.flush();
        }
    }

    private void chooseWrite(String incoming)
    {
        if (isServer)
            serverWrite(incoming);
        else
            clientWrite(incoming);
    }

    public void write(String incoming)
    {
        chooseWrite(incoming + "\r\n");
    }

    public void close()
    {
        if (channelHandlerCtx != null)
            channelHandlerCtx.close();
    }

    private void chooseRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        if (isServer)
            serverRead(ctx, incoming);
        else
            clientRead(ctx, incoming);
    }

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        Util.log(incoming);
        chooseRead(ctx, incoming);
    }

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}
