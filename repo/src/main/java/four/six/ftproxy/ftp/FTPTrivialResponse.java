package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPTrivialResponse implements FTPResponse {
    protected String[] args;
    protected FTPRelayHandler handler;

    public FTPTrivialResponse(String args[], FTPRelayHandler h)
    {
        this.args = args;
        handler = h;
    }

    // The 'default' behavior: pass on a response, unmodified, to the client
    // (eg: "500 bad command")
    @Override
    public String process()
    {
        return String.join(Util.SPACE, args) + Util.CRLF;
    }

    @Override
    public String getName()
    {
        return args[0];
    }
}
