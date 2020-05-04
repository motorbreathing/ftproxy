package four.six.ftproxy.ftp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import four.six.ftproxy.util.Util;
import four.six.ftproxy.netty.NettyUtil;

public class FTPUtil
{
    public static final String UNRECOGNIZED_COMMAND_STR =
        "500 Syntax error - command unrecognized\r\n";

    public static ChannelFuture
        getFTProxyServerChannel() throws Exception
    {
        return getFTProxyServerChannel(false);
    }

    public static ChannelFuture
        getFTProxyServerChannel(boolean sslStatus) throws Exception
    {
        return NettyUtil.getServerChannel(new FTProxyServerChannelInitializer(sslStatus)).sync();
    }

    public static InetSocketAddress processCommaSeparatedV4SocketAddress(String c)
    {
        String[] digits = c.split(Util.REGEX_SPLIT_BY_COMMA);
        if (digits.length != 6)
            return null;

        byte[] addr = new byte[4];
        addr[0] = Byte.parseByte(digits[0]);
        addr[1] = Byte.parseByte(digits[1]);
        addr[2] = Byte.parseByte(digits[2]);
        addr[3] = Byte.parseByte(digits[3]);

        int port = Integer.parseInt(digits[5]);
        port |= (Integer.parseInt(digits[4]) << 8);

        try {
            return new InetSocketAddress(InetAddress.getByAddress(addr), port);
        } catch (UnknownHostException e) {
            // This really shouldn't happen
            return null;
        }
    }

    public static String formatCommaSeparatedV4SocketAddress(byte[] addr, int port)
    {
        String s = Util.EMPTYSTRING;
        s += addr[0];
        s += Util.COMMA;
        s += addr[1];
        s += Util.COMMA;
        s += addr[2];
        s += Util.COMMA;
        s += addr[3];
        s += Util.COMMA;
        s += Integer.toString(port >> 8);
        s += Util.COMMA;
        s += Integer.toString(port & 0x000000ff);
        return s;
    }
}
