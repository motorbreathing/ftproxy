package four.six.ftproxy.ftp;

public class FTPAuthCommand extends FTPTrivialCommand
{
    public static final String COMMAND_STR = "AUTH";
    public static final String RESPONSE_STR = "234 Proceed with negotiation\r\n";

    public FTPAuthCommand(String args[], FTPRelayHandler handler)
    {
        super(args, handler);
    }

    @Override
    public String execute()
    {
        if (args[1].equalsIgnoreCase("SSL") || args[1].equalsIgnoreCase("TLS"))
        {
            handler.writeToClient(RESPONSE_STR);
            // Enable SSL - towards client
            handler.enableClientSSL();
            // Forward original "AUTH SSL/TLS" command to server
            super.execute();
        } else {
            handler.writeToClient(FTPUtil.UNRECOGNIZED_COMMAND_STR);
        }

        return null;
    }
}
