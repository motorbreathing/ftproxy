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

    private LineProvider clp;
    private LineProvider slp;
    private ChannelHandlerContext serverCtx;
    private ChannelHandlerContext clientCtx;
    private boolean serverReady;

    public LineHandler()
    {
        clp = new LineProvider();
    }

    private void welcomeClient() throws Exception
    {
        clientCtx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        clientCtx.write("It is " + new Date() + " now.\r\n");
        clientCtx.flush();
    }

    private void initiateServerConnect()
    {
        ChannelFuture cf =
            NettyUtil.getChannelToRemoteHost(new ServerChannelInitializer(this));
        ChannelFutureListener cfl =
            new ChannelFutureListener() {
                public void operationComplete(ChannelFuture f)
                {
                    if (f.isSuccess())
                    {
                        Util.log("Server channel connected");
                    } else {
                        Util.log("Server channel failed to connect");
                        clientCtx.writeAndFlush("FTP server unavailable\r\n");
                        clientCtx.close();
                    }
                }
            };
        cf.addListener(cfl);
    }

    // Process a new incoming client connection
    private void clientActive(ChannelHandlerContext ctx) throws Exception
    {
        Util.log("Client channel active!");
        clientCtx = ctx;
        welcomeClient();
        initiateServerConnect();
    }

    // Process a new backend/server connection
    private void serverActive(ChannelHandlerContext ctx)
    {
        Util.log("Backend server channel active!");
        serverCtx = ctx;
        slp = new LineProvider();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        if (clientCtx == null)
            clientActive(ctx);
        else
            serverActive(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
    {
        Util.log("LineHandler: removed");
        removePeerHandler(ctx);
    }

    private void removePeerHandler(ChannelHandlerContext ctx)
    {
        if (ctx == serverCtx)
            clientCtx.close();
        else if (ctx == clientCtx)
            serverCtx.close();
        else
            Util.log("removePeerHandler: unknown context");
    }

	public void clientRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        clp.add(incoming);
        if (serverCtx == null)
        {
            Util.log("Can't find server side context; stashing read");
            return;
        }
        flushToServer();
    }

    private void flushToServer()
    {
        while (true)
        {
            String line = clp.getLine();
            if (line == null)
                break;
            serverCtx.writeAndFlush(line + "\r\n");
            Util.log("[from client] " + line);
        }
    }

	public void serverRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        slp.add(incoming);
        flushToClient();
    }

    private void flushToClient()
    {
        while (true)
        {
            String line = slp.getLine();
            if (line == null)
                break;
            clientCtx.writeAndFlush(line + "\r\n");
            Util.log("[from server] " + line);
        }
    }

    private void chooseRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        if (ctx == serverCtx)
            serverRead(ctx, incoming);
        else
            clientRead(ctx, incoming);
    }

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception
    {
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
