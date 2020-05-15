package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class FTPDataRelayResponse extends FTPTrivialResponse {

    FTPDataRelayResponse(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    public static String getRelayResponse(InetSocketAddress saddr) {
        InetAddress iaddr = saddr.getAddress();
        byte[] ipaddr = iaddr.getAddress();
        int port = saddr.getPort();
        if (ipaddr.length == Util.IPV4_ADDRESS_LENGTH)
            return FTPPasvResponse.formatResponse(ipaddr, port);
        else if (ipaddr.length == Util.IPV6_ADDRESS_LENGTH)
            return FTPEpsvResponse.formatResponse(ipaddr, port);
        else
            Util.logWarning("FTP Relay response: bad address (length = " + ipaddr.length + ")");
        return null;
    }
}
