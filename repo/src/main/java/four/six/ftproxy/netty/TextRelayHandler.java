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
import io.netty.handler.ssl.SslHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.util.LineProvider;
import four.six.ftproxy.ssl.SSLHandlerProvider;

@Sharable
public class TextRelayHandler extends SimpleChannelInboundHandler<String> {

    // The individual stashes for client/server that hold on to received data
    // until fully formed lines are seen
    private LineProvider clientData;
    private LineProvider serverData;

    // The individual client/server handler contexts
    private ChannelHandlerContext serverCtx;
    private ChannelHandlerContext clientCtx;

    // Individual addresses that face client/server
    private InetAddress clientFacingAddress;
    private InetAddress serverFacingAddress;

    private boolean serverSSL;

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
        System.out.println("Initiating server-side connect");
        if (serverSSL)
            System.out.println("Server-side SSL enabled");
        else
            System.out.println("Server-side SSL disabled");
        TextRelayChannelInitializer selfPointer = 
                serverSSL ?  new TextRelayChannelInitializer() {
                                 @Override
                                 public boolean SSLEnabled()
                                 {
                                     return true;
                                 }

                                 @Override
                                 public ChannelHandler getProtocolHandler()
                                 {
                                     return getChannelHandler();
                                 }

                                 @Override
                                 public SslHandler getSSLHandler(Channel ch)
                                 {
                                     if (!SSLEnabled())
                                         return null;
                                     return SSLHandlerProvider.getClientSSLHandler(ch);
                                 }

                             }
                          :
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
        InetSocketAddress addr = ((SocketChannel)ctx.channel()).localAddress();
        Util.log("Active channel from client at: " + addr.toString());
        clientFacingAddress = addr.getAddress();
        initiateServerConnect();
    }

    private void initializeServer(ChannelHandlerContext ctx)
    {
        serverCtx = ctx;
        InetSocketAddress addr = ((SocketChannel)ctx.channel()).localAddress();
        Util.log("Active channel to server at: " + addr.toString());
        InetSocketAddress raddr = ((SocketChannel)ctx.channel()).remoteAddress();
        Util.log("Active channel to remote server at: " + raddr.toString());
        serverFacingAddress = addr.getAddress();
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
            throw new IllegalStateException("close: unknown channel handler context");
        }
    }

    public boolean clientSSLEnabled()
    {
        if (clientCtx == null)
            return false;
        ChannelHandler first = clientCtx.pipeline().first();
        return first instanceof SslHandler;
    }

    public boolean serverSSLEnabled()
    {
        if (serverCtx == null)
            return false;
        ChannelHandler first = serverCtx.pipeline().first();
        return first instanceof SslHandler;
    }

    public void enableClientSSL()
    {
        if (clientCtx == null)
            return; // This should not happen

        if (clientSSLEnabled())
            return;

        clientCtx.pipeline()
                 .addFirst(SSLHandlerProvider.getServerSSLHandler(clientCtx.channel()));
    }

    public void enableServerSSL()
    {
        Util.log("Enabling Server-side SSL");
        serverSSL = true;

        if (serverCtx == null)
            return;

        if (serverSSLEnabled())
            return;

        serverCtx.pipeline()
                 .addFirst(SSLHandlerProvider.getClientSSLHandler(serverCtx.channel()));
    }

    public void enableSSL(Object origin)
    {
        ChannelHandlerContext ctx = (ChannelHandlerContext)origin;
        if (ctx == clientCtx)
            enableClientSSL();
        else
            enableServerSSL();
    }

	public void clientRead(String incoming) throws Exception
    {
        clientData.add(incoming);
        if (serverCtx == null)
        {
            Util.log("Can't find server side context; stashing read");
            return;
        }
        flushToServer();
    }

	public void serverRead(String incoming) throws Exception
    {
        serverData.add(incoming);
        flushToClient();
    }

    public void replyToOrigin(String line, Object origin)
    {
        ChannelHandlerContext ctx = (ChannelHandlerContext)origin;
        if (ctx == clientCtx || ctx == serverCtx)
            ctx.writeAndFlush(line);
        else
            throw new IllegalStateException("replyToOrigin: unknown channel handler context");
    }

    public void relayToPeer(String line, Object origin)
    {
        ChannelHandlerContext ctx = (ChannelHandlerContext)origin;
        if (ctx == clientCtx)
            serverCtx.writeAndFlush(line);
        else if (ctx == serverCtx)
            clientCtx.writeAndFlush(line);
        else
            throw new IllegalStateException("relayToPeer: unknown channel handler context");
    }

    public String processCommand(String line)
    {
        return line + Util.CRLF;
    }

    public String processResponse(String line)
    {
        return line + Util.CRLF;
    }

    public void writeToClient(String line)
    {
        clientCtx.writeAndFlush(line);
    }

    public void writeToServer(String line)
    {
        Util.log("[wrote to server] " + line);
        serverCtx.writeAndFlush(line);
    }

    private void processCommandAndWrite(String line)
    {
        line = processCommand(line);
        if (line != null && line.length() > 0)
        {
            Util.log("[processed command] " + line);
            writeToServer(line);
        }
    }

    private void processResponseAndWrite(String line)
    {
        line = processResponse(line);
        if (line != null && line.length() > 0)
        {
            Util.log("[processed line] " + line);
            writeToClient(line);
        }
    }

    private void flushToServer()
    {
        while (true)
        {
            String line = clientData.getLine();
            if (line == null)
                break;
            Util.log("[from client] " + line);
            processCommandAndWrite(line);
        }
    }

    private void flushToClient()
    {
        while (true)
        {
            String line = serverData.getLine();
            if (line == null)
                break;
            Util.log("[from server] " + line);
            processResponseAndWrite(line);
        }
    }

    private void chooseRead(ChannelHandlerContext ctx, String incoming) throws Exception
    {
        if (ctx == serverCtx)
            serverRead(incoming);
        else if (ctx == clientCtx)
            clientRead(incoming);
        else
            throw new IllegalStateException("chooseRead: unknown channel handler context");
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
