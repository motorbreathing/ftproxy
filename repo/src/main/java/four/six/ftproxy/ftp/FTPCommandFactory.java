package four.six.ftproxy.ftp;

import four.six.ftproxy.ftp.FTPNoAction;

public class FTPCommandFactory {
    public FTPCommand getCommand(String line)
    {
        // default
        return new FTPNoAction();
    }
};
