package four.six.ftproxy.ftp;

import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

import four.six.ftproxy.util.Util;

public class FTPPortCommand extends FTPDataRelayCommand
{
    public static final String COMMAND_STR = "PORT";

    public FTPPortCommand(String args[], FTPRelayHandler handler)
    {
        super(args, handler);
        Util.log("FTP PORT command");
    }

    public static String formatCommand(byte[] addr, int port)
    {
        Util.log("FTP PORT command: format: port is " + port);
        String c = COMMAND_STR;
        c += Util.SPACE;
        c += FTPUtil.formatCommaSeparatedV4SocketAddress(addr, port);
        Util.log("FTP PORT command: formatted: " + c);
        return c + Util.CRLF;
    }

    protected InetSocketAddress processPortArgs()
    {
        String c = String.join(Util.SPACE, args);
        Util.log("About to process FTP PORT Command: " + c);
        c = c.substring(c.indexOf(Util.SPACE) + 1);
        InetSocketAddress address = FTPUtil.processCommaSeparatedV4SocketAddress(c);
        if (address == null)
        {
            Util.log("Bad address in PORT (" + String.join(Util.SPACE, args) + ")");
            return null;
        }
        return address;
    }

    @Override
    public String execute()
    {
        // We don't issue a response to PORT commands, and instead,
        // get down to the nitty gritties of setting up a data relay
        handler.startActiveRelay(processPortArgs());
        return null;
    }
}
