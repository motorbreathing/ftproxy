package four.six.ftproxy.util;

import java.nio.charset.Charset;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;

public class Util
{
    public static final String UTF8_STR = "UTF-8";
    public static final Charset UTF8charset = Charset.forName(UTF8_STR);
    public static final char LF = '\n';
    public static final char CR = '\r';
    public static final String CRLF = "\r\n";
    public static final String SPACE = " ";
    public static final String EMPTYSTRING = "";
    public static final String REGEX_SPLIT_BY_SPACES = "\\s+";

    private static final String DEFAULT_THIS_HOST_STR = "127.0.0.1";
    private static final String DEFAULT_THIS_PORT_STR = "8080";
    private static final String DEFAULT_REMOTE_HOST_STR = "127.0.0.1";
    private static final String DEFAULT_REMOTE_PORT_STR = "14646";
    private static final String DEFAULT_SERVER_BACKLOG_STR = "128";
    private static final String THIS_HOST_KEY = "host";
    private static final String THIS_PORT_KEY = "port";
    private static final String REMOTE_HOST_KEY = "remote-host";
    private static final String REMOTE_PORT_KEY = "remote-port";
    private static final String SERVER_BACKLOG_KEY = "server-backlog";

    public static final String THIS_HOST =
        System.getProperty(THIS_HOST_KEY, DEFAULT_THIS_HOST_STR);
    public static final int THIS_PORT =
        Integer.parseInt(System.getProperty(THIS_PORT_KEY, DEFAULT_THIS_PORT_STR));
    public static final String REMOTE_HOST =
        System.getProperty(REMOTE_HOST_KEY, DEFAULT_REMOTE_HOST_STR);
    public static final int REMOTE_PORT =
        Integer.parseInt(System.getProperty(REMOTE_PORT_KEY, DEFAULT_REMOTE_PORT_STR));
    public static final int SERVER_BACKLOG = 
        Integer.parseInt(System.getProperty(SERVER_BACKLOG_KEY,
                                            DEFAULT_SERVER_BACKLOG_STR));

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static {
        logger.setLevel(Level.FINE);
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);
    }

    public static void log(String msg)
    {
        logger.fine(msg);
    }
}
