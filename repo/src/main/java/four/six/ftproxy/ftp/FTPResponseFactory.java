package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPResponseFactory {
    public static FTPResponse getResponse(String line,
                                          FTPRelayHandler handler)
    {
        String[] args = line.split(Util.REGEX_SPLIT_BY_SPACES);

        // Server approved an "AUTH SSL/TLS"
        if (args[0].equals(FTPAuthResponse.RESPONSE_234_STR))
            return new FTPAuthResponse(args, handler);

        // The simple default
        return new FTPTrivialResponse(args, handler);
    }
};
