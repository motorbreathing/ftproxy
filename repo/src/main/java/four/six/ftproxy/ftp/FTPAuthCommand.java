package four.six.ftproxy.ftp;

public class FTPAuthCommand extends FTPTrivialCommand
{
    public static final String COMMAND_STR = "AUTH";
    public static final String RESPONSE_STR = "234 Proceed with negotiation\r\n";

    public FTPAuthCommand(String args[], Object origin, FTPRelayHandler h)
    {
        super(args, origin, h);
    }

    @Override
    public String execute()
    {
        if (args[1].equalsIgnoreCase("SSL") || args[1].equalsIgnoreCase("TLS"))
        {
            handler.replyToOrigin(RESPONSE_STR, origin);
            handler.enableSSL(origin);
        } else {
            handler.replyToOrigin(FTPUtil.UNRECOGNIZED_COMMAND_STR, origin);
        }

        // Nothing to be relayed to the peer; we've replied directly to the origin
        return null;
    }
}
