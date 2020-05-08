package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPTrivialCommand implements FTPCommand {
    protected String[] args;
    protected FTPRelayHandler handler;

    public FTPTrivialCommand(String args[], FTPRelayHandler h) {
        this.args = args;
        handler = h;
    }

    // The 'default' behavior: pass on a command, unmodified, to the server
    // (eg: "user someuser");
    @Override
    public String execute() {
        return String.join(Util.SPACE, args) + Util.CRLF;
    }

    @Override
    public String getName() {
        return args[0];
    }
}
