package four.six.ftproxy.ftp;

public class FTPNotImplementedResponse extends FTPTrivialResponse
{
    public static final String RESPONSE_502_STR = "502";

    public FTPNotImplementedResponse(String args[], FTPRelayHandler handler)
    {
        super(args, handler);
    }

    @Override
    public String process()
    {
        if (handler.dataSSLRequested()) 
        {
            handler.dataSSLRequested(false);
        } else if (handler.controlSSLRequested()) {
            handler.controlSSLRequested(false);
        }

        return super.process();
    }
}
