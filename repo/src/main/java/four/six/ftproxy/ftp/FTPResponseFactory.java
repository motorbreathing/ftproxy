package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPResponseFactory {
    public static FTPResponse getResponse(String line,
                                          FTPRelayHandler handler)
    {
        String[] args = line.split(Util.REGEX_SPLIT_BY_SPACES);

        // The simple default
        return new FTPTrivialResponse(args, handler);
    }
};
