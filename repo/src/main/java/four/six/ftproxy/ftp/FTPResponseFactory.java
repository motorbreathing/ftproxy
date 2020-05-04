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

        // Passive mode: server response to a PASV from client
        if (args[0].equals(FTPPasvResponse.RESPONSE_227_STR))
            return new FTPPasvResponse(args, handler);
        //
        // Passive mode: server response to an EPSV from client
        if (args[0].equals(FTPEpsvResponse.RESPONSE_229_STR))
            return new FTPEpsvResponse(args, handler);

        // The simple default
        return new FTPTrivialResponse(args, handler);
    }
};
