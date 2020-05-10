package four.six.ftproxy.ftp;

public class FTPAuthResponse extends FTPTrivialResponse {
    public static final String RESPONSE_234_STR = "234";

    public FTPAuthResponse(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    @Override
    public String process() {
        // Enable SSL - towards server
        handler.enableServerSSL();

        handler.controlSSLEnabled(true);
        handler.controlSSLRequested(false);

        // Pass the affirmative nod down to the client
        handler.writeToClient(super.process());
        // Once all done, enable SSL downstream
        handler.enableClientSSL();

        return null;
    }
}
