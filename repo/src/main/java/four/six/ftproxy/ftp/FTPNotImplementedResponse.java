package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPNotImplementedResponse extends FTPTrivialResponse {
    public static final String RESPONSE_502_STR = "502";

    public FTPNotImplementedResponse(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    @Override
    public String process() {
        if (handler.dataSSLRequested()) {
            Util.log("Server turned down TLS/SSL request on data channels");
            handler.dataSSLRequested(false);
        } else if (handler.dataSSLDisableRequested()) {
            Util.log("Server turned down TLS/SSL disable request on data channels");
            handler.dataSSLDisableRequested(false);
        } else if (handler.controlSSLRequested()) {
            Util.log("Server turned down TLS/SSL request on control channel");
            handler.controlSSLRequested(false);
        }

        return super.process();
    }
}
