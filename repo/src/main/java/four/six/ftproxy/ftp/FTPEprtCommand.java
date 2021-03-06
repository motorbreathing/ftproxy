package four.six.ftproxy.ftp;


import four.six.ftproxy.util.Util;
import java.net.InetSocketAddress;

public class FTPEprtCommand extends FTPDataRelayCommand {
    public static final String COMMAND_STR = "EPRT";

    public FTPEprtCommand(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    public static String formatCommand(byte[] addr, int port) {
        Util.logFinest("FTP EPRT command: format: port is " + port);
        String c = COMMAND_STR;
        c += Util.SPACE;
        c += FTPUtil.formatPipeDelimitedSocketAddress(addr, port);
        Util.logFine("FTP EPRT command: formatted: " + c);
        return c + Util.CRLF;
    }

    protected InetSocketAddress processEprtArgs() {
        String c = String.join(Util.SPACE, args);
        Util.logFine("About to process FTP EPRT Command: " + c);
        c = c.substring(c.indexOf(Util.SPACE) + 1);
        InetSocketAddress address = FTPUtil.processPipeDelimitedSocketAddress(c);
        if (address == null) {
            Util.logWarning("Bad address in EPRT (" + String.join(Util.SPACE, args) + ")");
            return null;
        }
        return address;
    }

    @Override
    public String execute() {
        // We don't issue a response to EPRT commands, and instead,
        // get down to the nitty gritties of setting up a data relay
        handler.startActiveRelay(processEprtArgs());
        return null;
    }
}
