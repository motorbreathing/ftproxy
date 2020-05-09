package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPAuthCommand extends FTPTrivialCommand {
    public static final String COMMAND_STR = "AUTH";
    public static final String RESPONSE_STR = "234 Proceed with negotiation\r\n";

    public FTPAuthCommand(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    @Override
    public String execute() {
        if (args[1].equalsIgnoreCase("SSL") || args[1].equalsIgnoreCase("TLS")) {
            // Forward original "AUTH SSL/TLS" command to server - only if SSL
            // termination is disabled
            if (!Util.getSSLTermination()) {
                handler.controlSSLRequested(true);
                return super.execute();
            }

            // Green flag to client
            handler.writeToClient(RESPONSE_STR);

            // Enable SSL - towards client
            handler.enableClientSSL();

            // No need to trouble the server on this topic, since we're configured to
            // terminate SSL at the proxy.
            return null;
        } else {
            handler.writeToClient(FTPUtil.UNRECOGNIZED_COMMAND_STR);
            // No need to trouble the server; we didn't like what we received
            // from the client, so we have complained straight back to the
            // client rather than forward the dubious command to the server
            return null;
        }
    }
}
