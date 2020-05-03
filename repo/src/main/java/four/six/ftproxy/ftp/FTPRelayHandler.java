package four.six.ftproxy.ftp;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.net.InetSocketAddress;

import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.DataRelayHandler;
import four.six.ftproxy.netty.TextRelayHandler;
import four.six.ftproxy.netty.DataRelayChannelInitializer;

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

    // Handle data relay: active mode
    public void relayFromClient(InetSocketAddress addr)
    {
        DataRelayHandler handler = new DataRelayHandler(addr);
        DataRelayChannelInitializer ci =
            new DataRelayChannelInitializer() {
                @Override
                public ChannelHandler getProtocolHandler()
                {
                    return handler;
                }
            };
        Channel ch = NettyUtil.getListenerChannel(serverFacingAddress, ci).channel();
        InetSocketAddress localAddr = ((SocketChannel)ch).localAddress();
        writeToServer(FTPDataTransferCommand.getTransferCommand(localAddr));
    }

    // Handle data relay: passive mdoe
    public void relayFromServer(InetSocketAddress addr)
    {

    }
}
