package four.six.ftproxy.ftp;

import io.netty.channel.ChannelFuture;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import four.six.ftproxy.util.Util;

public class FTPPortCommand extends FTPDataTransferCommand
{
    public static final String COMMAND_STR = "PORT";

    public FTPPortCommand(String args[], FTPRelayHandler handler)
    {
        super(args, handler);
        Util.log("FTP PORT command");
    }

    public static String formatCommand(byte[] addr, int port)
    {
        Util.log("FTPPortCommand: format: port is " + port);
        String c = COMMAND_STR;
        c += Util.SPACE;
        c += addr[0];
        c += Util.COMMA;
        c += addr[1];
        c += Util.COMMA;
        c += addr[2];
        c += Util.COMMA;
        c += addr[3];
        c += Util.COMMA;
        c += Integer.toString(port >> 8);
        c += Util.COMMA;
        c += Integer.toString(port & 0x000000ff);
        Util.log("FTP Port command: formatted: " + c);
        return c + Util.CRLF;
    }

    protected InetSocketAddress processPortArgs()
    {
        String c = String.join(Util.SPACE, args);
        Util.log("About to process FTP PORT Command: " + c);
        c = c.substring(c.indexOf(Util.SPACE) + 1);
        String[] digits = c.split(Util.REGEX_SPLIT_BY_COMMA);
        if (digits.length != 6)
        {
            Util.log("Badly formatted PORT command (" + String.join(Util.SPACE, args) + ")");
            return null;
        }

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

    @Override
    public String execute()
    {
        // We don't issue a response to PORT commands, and instead,
        // get down to the nitty gritties of setting up a data relay
        handler.relayToClient(processPortArgs());
        return null;
    }
}
