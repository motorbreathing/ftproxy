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

    public static InetSocketAddress processCommaDelimitedV4SocketAddress(String c)
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

    // EPRT
    public static InetSocketAddress processPipeDelimitedSocketAddress(String c)
    {
        int first = c.indexOf(Util.PIPE);
        if (first == -1)
            return null;
        int second = c.indexOf(Util.PIPE, first + 1);
        if (second == -1)
            return null;
        int third = c.indexOf(Util.PIPE, second + 1);
        if (third == -1)
            return null;
        int fourth = c.indexOf(Util.PIPE, third + 1);
        if (fourth == -1)
            return null;

        // Phew.

        try {
            int port = Integer.parseInt(c.substring(third + 1, fourth));
            Util.log("processPipeDelimitedSocketAddress: port is " + port);
            if (port == -1)
                return null;
            InetAddress addr = InetAddress.getByName(c.substring(second + 1, third));
            Util.log("processPipeDelimitedSocketAddress: addr is " + addr.toString());
            return new InetSocketAddress(addr, port);
        } catch (Exception e) {
            Util.log("Failed to parse address string: " + e.toString());
            return null;
        }
    }

    // EPRT
    public static String formatPipeDelimitedSocketAddress(byte[] addr, int port)
    {
        if (addr.length != Util.IPV4_ADDRESS_LENGTH && addr.length != Util.IPV6_ADDRESS_LENGTH) {
            Util.log("FTP format address: bad address (length = " + addr.length + ")");
            return null;
        }
        String s = Util.EMPTYSTRING;
        s += Util.PIPE;
        if (addr.length == Util.IPV4_ADDRESS_LENGTH)
            s += "1";
        else
            s += "2";
        s += Util.PIPE;
        try {
            s += InetAddress.getByAddress(addr).toString();
        } catch (UnknownHostException e) {
            // This really shouldn't happen...
            return null;
        }
        s += Util.PIPE;
        s += Integer.toString(port);
        s += Util.PIPE;
        return s;
    }

    // PORT, PASV
    public static String formatCommaDelimitedV4SocketAddress(byte[] addr, int port)
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

    // EPSV
    public static int processPipeDelimitedV6SocketAddress(String c)
    {
        int lastindex = c.lastIndexOf(Util.PIPE);
        if (lastindex == -1)
            return -1;
        int nextindex = c.lastIndexOf(Util.PIPE, lastindex - 1);
        if (nextindex == -1)
            return -1;
        return Integer.parseInt(c.substring(nextindex + 1, lastindex));
    }
}
