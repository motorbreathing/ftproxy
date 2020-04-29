package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPCommandFactory {
    public static FTPCommand getCommand(String line,
                                        FTPRelayHandler handler)
    {
        String[] args = line.split(Util.REGEX_SPLIT_BY_SPACES);

        if (args[0].equalsIgnoreCase(FTPAuthCommand.COMMAND_STR))
            return new FTPAuthCommand(args, handler);

        // The simple default
        return new FTPTrivialCommand(args, handler);
    }
};
