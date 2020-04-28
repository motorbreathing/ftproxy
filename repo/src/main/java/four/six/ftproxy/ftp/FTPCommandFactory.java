package four.six.ftproxy.ftp;

import four.six.ftproxy.ftp.FTPTrivialCommand;

public class FTPCommandFactory {
    public static FTPCommand getCommand(String line)
    {
        // default
        return new FTPTrivialCommand(line);
    }
};
