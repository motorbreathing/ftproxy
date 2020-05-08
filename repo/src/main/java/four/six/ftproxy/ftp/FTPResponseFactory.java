package four.six.ftproxy.ftp;

import four.six.ftproxy.util.Util;

public class FTPResponseFactory {
    public static FTPResponse getResponse(String line, FTPRelayHandler handler) {
        String[] args = line.split(Util.REGEX_SPLIT_BY_SPACES);

        // The server nodded agreement to something
        if (args[0].equals(FTPOkResponse.RESPONSE_200_STR)) return new FTPOkResponse(args, handler);

        // The server nodded agreement to something
        if (args[0].equals(FTPNotImplementedResponse.RESPONSE_502_STR))
            return new FTPNotImplementedResponse(args, handler);

        // Server approved an "AUTH SSL/TLS"
        if (args[0].equals(FTPAuthResponse.RESPONSE_234_STR)) return new FTPAuthResponse(args, handler);

        // Passive mode: server response to a PASV from client
        if (args[0].equals(FTPPasvResponse.RESPONSE_227_STR)) return new FTPPasvResponse(args, handler);
        //
        // Passive mode: server response to an EPSV from client
        if (args[0].equals(FTPEpsvResponse.RESPONSE_229_STR)) return new FTPEpsvResponse(args, handler);

        // The simple default
        return new FTPTrivialResponse(args, handler);
    }
}
;
