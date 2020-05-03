package four.six.ftproxy.ftp;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

import java.net.InetSocketAddress;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.DataRelayHandler;
import four.six.ftproxy.netty.TextRelayHandler;
import four.six.ftproxy.netty.DataRelayChannelInitializer;
import four.six.ftproxy.ssl.SSLHandlerProvider;

public class FTPRelayHandler extends TextRelayHandler
{
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
    // listener, send it's address to the server, and complete the data
    // relay
    public void completeRelayToClient(DataRelayHandler handler)
    {
        ChannelInitializer ci = getChannelInitializer(handler, false, true);
        Channel ch = NettyUtil.getListenerChannel(serverFacingAddress, ci).channel();
        InetSocketAddress localAddr = (InetSocketAddress)ch.localAddress();
        writeToServer(FTPDataTransferCommand.getTransferCommand(localAddr));
    }

    // The provided argument is the address specified in a PORT/EPRT command
    public void relayToClient(InetSocketAddress addr)
    {
        DataRelayHandler handler = new DataRelayHandler();
        ChannelInitializer ci = getChannelInitializer(handler, false, false);
        ChannelFuture cf = NettyUtil.getChannelToAddress(addr, ci);
        ChannelFutureListener cfl =
            new ChannelFutureListener() {
                public void operationComplete(ChannelFuture f)
                {
                    if (f.isSuccess())
                    {
                        Util.log("DATA: client channel connected");
                        FTPRelayHandler.this.completeRelayToClient(handler);
                    } else {
                        Util.log("DATA: client channel failed to connect");
                    }
                }
            };
        cf.addListener(cfl);
    }

    // Handle data relay: passive mode
    public void relayFromServer(InetSocketAddress addr)
    {

    }
}
