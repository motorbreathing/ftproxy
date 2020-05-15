package four.six.ftproxy.ftp;


import four.six.ftproxy.util.Util;
import java.net.InetSocketAddress;

public class FTPPortCommand extends FTPDataRelayCommand {
    public static final String COMMAND_STR = "PORT";

    public FTPPortCommand(String args[], FTPRelayHandler handler) {
        super(args, handler);
        Util.logFinest("FTP PORT command");
    }

    public static String formatCommand(byte[] addr, int port) {
        Util.logFinest("FTP PORT command: format: port is " + port);
        String c = COMMAND_STR;
        c += Util.SPACE;
        c += FTPUtil.formatCommaDelimitedV4SocketAddress(addr, port);
        Util.logFine("FTP PORT command: formatted: " + c);
        return c + Util.CRLF;
    }

    protected InetSocketAddress processPortArgs() {
        String c = String.join(Util.SPACE, args);
        Util.logFine("About to process FTP PORT Command: " + c);
        c = c.substring(c.indexOf(Util.SPACE) + 1);
        InetSocketAddress address = FTPUtil.processCommaDelimitedV4SocketAddress(c);
        if (address == null) {
            Util.logWarning("Bad address in PORT (" + String.join(Util.SPACE, args) + ")");
            return null;
        }
        return address;
    }

    @Override
    public String execute() {
        // We don't issue a response to PORT commands, and instead,
        // get down to the nitty gritties of setting up a data relay
        handler.startActiveRelay(processPortArgs());
        return null;
    }
}
