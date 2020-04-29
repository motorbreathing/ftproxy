package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPTrivialCommand implements FTPCommand {
    protected String[] args;
    protected FTPRelayHandler handler;
    protected Object origin;

    public FTPTrivialCommand(String args[], Object origin, FTPRelayHandler h)
    {
        this.args = args;
        this.origin = origin;
        handler = h;
    }

    // The 'default' behavior: pass on a command, unmodified, to the server
    // (eg: "user someuser");
    @Override
    public String execute()
    {
        String original = "";
        for (String word : args)
        {
            original += word;
            // XXX
            original += " ";
        }
        return original + Util.CRLF;
    }

    @Override
    public String getName()
    {
        return getClass().getName();
    }
}
