package four.six.ftproxy.ftp;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import four.six.ftproxy.util.Util;

public class FTPDataTransferCommand extends FTPTrivialCommand {

    FTPDataTransferCommand(String args[], FTPRelayHandler handler)
    {
        super(args, handler);
    }

    public static String getTransferCommand(InetSocketAddress saddr)
    {
        InetAddress iaddr = saddr.getAddress();
        byte[] ipaddr = iaddr.getAddress();
        int port = saddr.getPort();
        if (ipaddr.length == Util.IPV4_ADDRESS_LENGTH)
            return FTPPortCommand.formatCommand(ipaddr, port);
        else if (ipaddr.length != Util.IPV6_ADDRESS_LENGTH)
            return null;
        return null;
    }
}
