package four.six.ftproxy.ftp;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

import java.util.Set;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.DataRelayHandler;
import four.six.ftproxy.netty.TextRelayHandler;
import four.six.ftproxy.netty.DataRelayChannelInitializer;
import four.six.ftproxy.ssl.SSLHandlerProvider;

public class FTPRelayHandler extends TextRelayHandler
{
    private Set<DataRelayHandler> aliveDataSessions = ConcurrentHashMap.newKeySet();

    @Override
    public String processCommand(String line)
    {
        line = line.trim();
        if (line.length() > 0)
            return FTPCommandFactory.getCommand(line, this).execute();

        return null;
    }

    @Override
    public String processResponse(String line)
    {
        line = line.trim();
        if (line.length() > 0)
            return FTPResponseFactory.getResponse(line, this).process();

        return null;
    }

    private ChannelInitializer<? extends Channel>
        getChannelInitializer(ChannelHandler handler, boolean sslEnabled,
                              boolean client)
    {
        DataRelayChannelInitializer ci = 

            sslEnabled ?

            new DataRelayChannelInitializer() {
                @Override
                public ChannelHandler getSSLHandler(Channel ch)
                {
                    if (client)
                        return SSLHandlerProvider.getClientSSLHandler(ch);
                    else
                        return SSLHandlerProvider.getServerSSLHandler(ch);
                }

                @Override
                public ChannelHandler getProtocolHandler()
                {
                    return handler;
                };
            }

            : 

            new DataRelayChannelInitializer() {
                @Override
                public ChannelHandler getProtocolHandler()
                {
                    return handler;
                }
            };

        return ci;
    }

    // We've connected to the client-provided address; now establish a
    // listener, send it's address to the server, and finish the data
    // relay
    public boolean finishActiveRelay(DataRelayHandler handler)
    {
        ChannelInitializer<? extends Channel> ci =
            getChannelInitializer(handler, false, true);
        ChannelFuture cf = NettyUtil.getListenerChannel(serverFacingAddress, ci);
        if (cf == null) {
            Util.log("Active relay: failed to create listener");
            return false;
        }
        InetSocketAddress localAddr = (InetSocketAddress)cf.channel().localAddress();
        String command = FTPDataRelayCommand.getRelayCommand(localAddr);
        if (command != null) {
            aliveDataSessions.add(handler);
            writeToServer(FTPDataRelayCommand.getRelayCommand(localAddr));
            Util.log("Active relay: " + command);
        } else {
            Util.log("Active relay: failed to format command");
            return false;
        }
        return true;
    }

    public void finishPassiveRelay(DataRelayHandler handler, InetSocketAddress addr)
    {
        ChannelInitializer<? extends Channel> ci =
            getChannelInitializer(handler, false, false);
        ChannelFuture cf = NettyUtil.getChannelToAddress(addr, ci);
        ChannelFutureListener cfl =
            new ChannelFutureListener() {
                public void operationComplete(ChannelFuture f)
                {
                    if (f.isSuccess())
                    {
                        Util.log("Passive relay: server channel connected");
                        f.channel().closeFuture().addListener(
                                new ChannelFutureListener() {
                                    public void operationComplete(ChannelFuture f)
                                    {
                                        Util.log("Passive relay: session done");
                                        aliveDataSessions.remove(handler);
                                    }
                                });
                    } else {
                        Util.log("Passive relay: server channel failed to connect");
                        aliveDataSessions.remove(handler);
                        handler.closeSession();
                    }
                }
            };
        cf.addListener(cfl);

    }

    // The provided argument is the address specified in a PORT/EPRT command
    public void startActiveRelay(InetSocketAddress addr)
    {
        DataRelayHandler handler = new DataRelayHandler();
        ChannelInitializer<? extends Channel> ci =
            getChannelInitializer(handler, false, false);
        ChannelFuture cf = NettyUtil.getChannelToAddress(addr, ci);
        ChannelFutureListener cfl =
            new ChannelFutureListener() {
                public void operationComplete(ChannelFuture f)
                {
                    if (f.isSuccess())
                    {
                        Util.log("Active relay: client channel connected");
                        f.channel().closeFuture().addListener(
                                new ChannelFutureListener() {
                                    public void operationComplete(ChannelFuture f)
                                    {
                                        Util.log("Active relay: session done");
                                        aliveDataSessions.remove(handler);
                                    }
                                });
                        if (!finishActiveRelay(handler))
                        {
                            Util.log("Active relay: failed to finish");
                            f.channel().close();
                        }
                    } else {
                        Util.log("Active relay: client channel failed to connect");
                    }
                }
            };
        cf.addListener(cfl);
    }

    // Handle data relay: passive mode
    // The specified argument is a server-provided listener address
    public void startPassiveRelay(InetSocketAddress addr)
    {
        DataRelayHandler handler = new DataRelayHandler() {
             @Override
             public void channelActive(ChannelHandlerContext ctx) throws Exception
             {
                 if (oneCtx == null)
                     finishPassiveRelay(this, addr);
                 initContext(ctx);
             }
        };
        ChannelInitializer<? extends Channel> ci =
            getChannelInitializer(handler, false, false);
        ChannelFuture cf = NettyUtil.getListenerChannel(clientFacingAddress, ci);
        if (cf == null) {
            Util.log("Passive relay: failed to create listener");
            return;
        }
        InetSocketAddress localAddr = (InetSocketAddress)cf.channel().localAddress();
        String response = FTPDataRelayResponse.getRelayResponse(localAddr);
        if (response != null) {
            aliveDataSessions.add(handler);
            Util.log("Passive relay: " + response);
            writeToClient(response);
        } else {
            Util.log("Passive relay: failed to format response");
            cf.channel().close();
            return;
        }
    }

    public void startPassiveRelay(int port)
    {
        startPassiveRelay(new InetSocketAddress(serverFacingAddress, port));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
    {
        Util.log("FTPRelayHandler: removed");
        for (DataRelayHandler handler : aliveDataSessions)
            handler.closeSession();
        aliveDataSessions.clear();
        super.handlerRemoved(ctx);
    }
}
