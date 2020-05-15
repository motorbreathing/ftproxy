package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class FTPDataRelayCommand extends FTPTrivialCommand {

    FTPDataRelayCommand(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    public static String getRelayCommand(InetSocketAddress saddr) {
        InetAddress iaddr = saddr.getAddress();
        byte[] ipaddr = iaddr.getAddress();
        int port = saddr.getPort();
        if (ipaddr.length == Util.IPV4_ADDRESS_LENGTH)
            return FTPPortCommand.formatCommand(ipaddr, port);
        else if (ipaddr.length == Util.IPV6_ADDRESS_LENGTH)
            return FTPEprtCommand.formatCommand(ipaddr, port);
        else
            Util.logWarning("FTP Relay command: bad address (length = " + ipaddr.length + ")");
        return null;
    }
}
