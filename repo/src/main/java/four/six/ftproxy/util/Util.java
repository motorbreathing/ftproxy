package four.six.ftproxy.util;

import java.nio.charset.Charset;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

public class Util {
    private static Properties defaultProperties;
    private static Properties configProperties;

    public static final String APPNAME = "ftproxy";
    public static final String CONFIG_FILENAME = APPNAME + ".properties";

    public static final String UTF8_STR = "UTF-8";
    public static final Charset UTF8charset = Charset.forName(UTF8_STR);
    public static final char LF = '\n';
    public static final char CR = '\r';
    public static final String CRLF = "\r\n";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String LEFT_PARA = "(";
    public static final String RIGHT_PARA = ")";
    public static final String PIPE = "|";
    public static final String EMPTYSTRING = "";
    public static final String REGEX_SPLIT_BY_SPACES = "\\s+";
    public static final String REGEX_SPLIT_BY_COMMA = "\\s*,\\s*";

    public static final String DEFAULT_THIS_HOST_STR = "127.0.0.1";
    public static final String DEFAULT_THIS_PORT_STR = "8080";
    public static final String DEFAULT_REMOTE_HOST_STR = "127.0.0.1";
    public static final String DEFAULT_REMOTE_PORT_STR = "14646";
    public static final String DEFAULT_SERVER_BACKLOG_STR = "128";
    public static final String DEFAULT_READ_TIMEOUT_STR = "30";
    public static final String DEFAULT_TERMINATE_SSL_STR = "false";

    public static final String THIS_HOST_KEY = "host";
    public static final String THIS_PORT_KEY = "port";
    public static final String REMOTE_HOST_KEY = "remote-host";
    public static final String REMOTE_PORT_KEY = "remote-port";
    public static final String SERVER_BACKLOG_KEY = "server-backlog";
    public static final String READ_TIMEOUT_KEY = "read-timeout";
    public static final String TERMINATE_SSL_KEY = "terminate-ssl";

    public static final String LOOPBACK_IPV6 = "::1";
    public static final String LOCAL_HOST = "localhost";
    public static final String THIS_HOST = System.getProperty(THIS_HOST_KEY, DEFAULT_THIS_HOST_STR);
    public static final int THIS_PORT =
            Integer.parseInt(System.getProperty(THIS_PORT_KEY, DEFAULT_THIS_PORT_STR));
    public static final String REMOTE_HOST =
            System.getProperty(REMOTE_HOST_KEY, DEFAULT_REMOTE_HOST_STR);
    public static final int REMOTE_PORT =
            Integer.parseInt(System.getProperty(REMOTE_PORT_KEY, DEFAULT_REMOTE_PORT_STR));
    public static final int SERVER_BACKLOG =
            Integer.parseInt(System.getProperty(SERVER_BACKLOG_KEY, DEFAULT_SERVER_BACKLOG_STR));
    public static final int READ_TIMEOUT_SECONDS =
            Integer.parseInt(System.getProperty(READ_TIMEOUT_KEY, DEFAULT_READ_TIMEOUT_STR));
    public static final boolean TERMINATE_SSL =
            Boolean.parseBoolean(System.getProperty(TERMINATE_SSL_KEY, DEFAULT_TERMINATE_SSL_STR));

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static final int dataPortMin = 1025;
    public static final int dataPortMax = 65535;

    public static final int IPV4_ADDRESS_LENGTH = 4;
    public static final int IPV6_ADDRESS_LENGTH = 16;

    static {
        setupLogging();

        defaultProperties = new Properties();
        defaultProperties.setProperty(THIS_HOST_KEY, THIS_HOST);
        defaultProperties.setProperty(THIS_PORT_KEY, Integer.toString(THIS_PORT));
        defaultProperties.setProperty(REMOTE_HOST_KEY, REMOTE_HOST);
        defaultProperties.setProperty(REMOTE_PORT_KEY, Integer.toString(REMOTE_PORT));
        defaultProperties.setProperty(SERVER_BACKLOG_KEY, Integer.toString(SERVER_BACKLOG));
        defaultProperties.setProperty(READ_TIMEOUT_KEY, Integer.toString(READ_TIMEOUT_SECONDS));
        defaultProperties.setProperty(TERMINATE_SSL_KEY, Boolean.toString(TERMINATE_SSL));
        configProperties = new Properties(defaultProperties);
        loadConfigFromFile(CONFIG_FILENAME);
    }

    private static void setupLogging() {
        logger.setLevel(Level.FINE);
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);
    }

    public static String getRemoteHost() {
        return configProperties.getProperty(REMOTE_HOST_KEY);
    }

    public static int getRemotePort() {
        return Integer.parseInt(configProperties.getProperty(REMOTE_PORT_KEY));
    }

    public static boolean getSSLTermination()
    {
        return Boolean.parseBoolean(configProperties.getProperty(TERMINATE_SSL_KEY));
    }

    public static void setSSLTermination(boolean f)
    {
        setConfigProperty(TERMINATE_SSL_KEY, Boolean.toString(f));
    }

    private static void setConfigProperty(String key, String value) {
        configProperties.setProperty(key, value);
    }

    public static boolean loadConfigFromFile(String filename) {
        File f = new File(filename);
        if (!f.exists() || !f.canRead()) {
            Util.log("Unable to access config file: " + filename);
            return false;
        }

        try {
            configProperties.load(new FileInputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void log(String msg) {
        logger.fine(msg);
    }
}
