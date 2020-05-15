package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPEpsvResponse extends FTPDataRelayResponse {
    public static final String COMMAND_STR = "EPSV";
    public static final String RESPONSE_229_STR = "229";
    public static final String RESPONSE_DESC_STR = "Entering Extended Passive Mode";

    public FTPEpsvResponse(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    public static String formatResponse(byte[] addr, int port) {
        Util.logFinest("FTP EPSV response: format: port is " + port);
        return formatV6Response(port);
    }

    private static String formatV6Response(int port) {
        String r = RESPONSE_229_STR;
        r += Util.SPACE;
        r += RESPONSE_DESC_STR;
        r += Util.SPACE;
        r += Util.LEFT_PARA;
        r += Util.PIPE;
        r += Util.PIPE;
        r += Util.PIPE;
        r += Integer.toString(port);
        r += Util.PIPE;
        r += Util.RIGHT_PARA;
        Util.logFine("FTP EPSV response: formatted: " + r);
        return r + Util.CRLF;
    }

    protected int processEpsvArgs() {
        String c = String.join(Util.SPACE, args);
        Util.logFine("About to process FTP EPSV response: " + c);
        c = c.substring(c.indexOf(Util.LEFT_PARA) + 1, c.indexOf(Util.RIGHT_PARA));
        return FTPUtil.processPipeDelimitedV6SocketAddress(c);
    }

    @Override
    public String process() {
        handler.startPassiveRelay(processEpsvArgs());
        return null;
    }
}
