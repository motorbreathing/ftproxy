package four.six.ftproxy.netty;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.util.LineProvider;
import four.six.ftproxy.netty.NettyUtil;

@Sharable
public class TextRelayHandler extends SimpleChannelInboundHandler<String> {

    private LineProvider clientData;
    private LineProvider serverData;
    private ChannelHandlerContext serverCtx;
    private ChannelHandlerContext clientCtx;
    private boolean serverReady;

    public TextRelayHandler()
    {
        clientData = new LineProvider();
    }

    public ChannelHandler getChannelHandler()
    {
        return this;
    }

    private void initiateServerConnect()
    {
        TextRelayChannelInitializer selfPointer = 
                new TextRelayChannelInitializer() {
                    @Override
                    public ChannelHandler getProtocolHandler()
                    {
                        return getChannelHandler();
                    }
                };
        ChannelFuture cf = NettyUtil.getChannelToRemoteHost(selfPointer);

        ChannelFutureListener cfl =
            new ChannelFutureListener() {
                public void operationComplete(ChannelFuture f)
                {
                    if (f.isSuccess())
                    {
                        Util.log("Server channel connected");
                    } else {
                        Util.log("Server channel failed to connect");
                        clientCtx.writeAndFlush("Server unavailable\r\n");
                        clientCtx.close();
                    }
                }
            };
        cf.addListener(cfl);
    }

    private void initializeClient(ChannelHandlerContext ctx)
    {
        clientCtx = ctx;
        initiateServerConnect();
    }

    private void initializeServer(ChannelHandlerContext ctx)
    {
        serverCtx = ctx;
        serverData = new LineProvider();
    }

    // Process a new incoming client connection
    private void clientActive(ChannelHandlerContext ctx) throws Exception
    {
        Util.log("Client channel active");
        initializeClient(ctx);
    }

    // Process a new backend/server connection
    private void serverActive(ChannelHandlerContext ctx)
    {
        Util.log("Backend server channel active");
        initializeServer(ctx);
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
        Util.log("TextRelayHandler: removed");
        removePeerHandler(ctx);
    }

    private void removePeerHandler(ChannelHandlerContext ctx)
    {
        if (ctx == serverCtx) {
            clientCtx.close();
        } else if (ctx == clientCtx) {
            serverCtx.close();
        } else {
            throw new IllegalStateException("Attempt to close unknown channel handler context");
        }
    }

	public void clientRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        clientData.add(incoming);
        if (serverCtx == null)
        {
            Util.log("Can't find server side context; stashing read");
            return;
        }
        flushToServer();
    }

    public String process(String line)
    {
        // Default implementation
        return line;
    }

    private void processAndWrite(ChannelHandlerContext ctx, String line)
    {
        line = process(line);
        ctx.writeAndFlush(line + Util.CRLF);
    }

    private void flushToServer()
    {
        while (true)
        {
            String line = clientData.getLine();
            if (line == null)
                break;
            processAndWrite(serverCtx, line);
            Util.log("[from client] " + line);
        }
    }

	public void serverRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        serverData.add(incoming);
        flushToClient();
    }

    private void flushToClient()
    {
        while (true)
        {
            String line = serverData.getLine();
            if (line == null)
                break;
            processAndWrite(clientCtx, line);
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
