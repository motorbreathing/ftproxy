package four.six.ftproxy.ftp;

import java.net.InetSocketAddress;

import four.six.ftproxy.util.Util;

public class FTPPasvResponse extends FTPDataRelayResponse
{
    public static final String COMMAND_STR = "PASV";
    public static final String RESPONSE_227_STR = "227";
    public static final String RESPONSE_DESC_STR = "Entering Passive Mode";

    public FTPPasvResponse(String args[], FTPRelayHandler handler)
    {
        super(args, handler);
    }

    public static String formatResponse(byte[] addr, int port)
    {
        Util.log("FTP PASV response: format: port is " + port);
        if (addr.length == Util.IPV4_ADDRESS_LENGTH) {
            return formatV4Response(addr, port);
        } else {
            Util.log("Format FTP PASV response: invalid address (bad length: " + addr.length + ")");
            return null;
        }
    }

    private static String formatV4Response(byte[] addr, int port)
    {
        String r = RESPONSE_227_STR;
        r += Util.SPACE;
        r += RESPONSE_DESC_STR;
        r += Util.SPACE;
        r += Util.LEFT_PARA;
        r += FTPUtil.formatCommaDelimitedV4SocketAddress(addr, port);
        r += Util.RIGHT_PARA;
        Util.log("FTP PASV response: formatted: " + r);
        return r + Util.CRLF;
    }

    protected InetSocketAddress processPasvArgs()
    {
        String c = String.join(Util.SPACE, args);
        Util.log("About to process FTP PASV response: " + c);
        c = c.substring(c.indexOf(Util.LEFT_PARA) + 1, c.indexOf(Util.RIGHT_PARA));
        InetSocketAddress address = FTPUtil.processCommaDelimitedV4SocketAddress(c);
        if (address == null) {
            Util.log("Bad address in PASV (" + String.join(Util.SPACE, args) + ")");
            return null;
        }
        return address;
    }

    @Override
    public String process()
    {
        handler.startPassiveRelay(processPasvArgs());
        return null;
    }
}
