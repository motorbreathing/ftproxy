package four.six.ftproxy.ftp;

public class FTPOkResponse extends FTPTrivialResponse {
    public static final String RESPONSE_200_STR = "200";

    public FTPOkResponse(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    @Override
    public String process() {
        if (handler.dataSSLRequested()) {
            handler.dataSSLRequested(false);
            handler.dataSSLEnabled(true);
        } else if (handler.dataSSLDisableRequested()) {
            handler.dataSSLDisableRequested(false);
            handler.dataSSLEnabled(false);
        }

        return super.process();
    }
}
