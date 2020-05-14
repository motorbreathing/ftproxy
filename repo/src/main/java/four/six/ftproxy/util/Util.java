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
    public static final String DEFAULT_IMPLICIT_SSL_STR = "false";
    public static final String DEFAULT_PROPERTIES_PATH_STR = CONFIG_FILENAME;
    public static final String DEFAULT_LOGLEVEL_STR = "info";

    public static final String THIS_HOST_KEY = "host";
    public static final String THIS_PORT_KEY = "port";
    public static final String REMOTE_HOST_KEY = "remote-host";
    public static final String REMOTE_PORT_KEY = "remote-port";
    public static final String SERVER_BACKLOG_KEY = "server-backlog";
    public static final String READ_TIMEOUT_KEY = "read-timeout";
    public static final String TERMINATE_SSL_KEY = "terminate-ssl";
    public static final String IMPLICIT_SSL_KEY = "implicit-ssl";
    public static final String PROPERTIES_PATH_KEY = "path-to-properties";
    public static final String LOGLEVEL_KEY = "log-level";

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
    public static final boolean IMPLICIT_SSL =
            Boolean.parseBoolean(System.getProperty(IMPLICIT_SSL_KEY, DEFAULT_IMPLICIT_SSL_STR));
    public static final String PROPERTIES_PATH =
            System.getProperty(PROPERTIES_PATH_KEY, DEFAULT_PROPERTIES_PATH_STR);
    public static final String LOGLEVEL =
            System.getProperty(LOGLEVEL_KEY, DEFAULT_LOGLEVEL_STR);

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static final int dataPortMin = 1025;
    public static final int dataPortMax = 65535;

    public static final int IPV4_ADDRESS_LENGTH = 4;
    public static final int IPV6_ADDRESS_LENGTH = 16;

    static {
        defaultProperties = new Properties();
        defaultProperties.setProperty(THIS_HOST_KEY, THIS_HOST);
        defaultProperties.setProperty(THIS_PORT_KEY, Integer.toString(THIS_PORT));
        defaultProperties.setProperty(REMOTE_HOST_KEY, REMOTE_HOST);
        defaultProperties.setProperty(REMOTE_PORT_KEY, Integer.toString(REMOTE_PORT));
        defaultProperties.setProperty(SERVER_BACKLOG_KEY, Integer.toString(SERVER_BACKLOG));
        defaultProperties.setProperty(READ_TIMEOUT_KEY, Integer.toString(READ_TIMEOUT_SECONDS));
        defaultProperties.setProperty(TERMINATE_SSL_KEY, Boolean.toString(TERMINATE_SSL));
        defaultProperties.setProperty(TERMINATE_SSL_KEY, Boolean.toString(TERMINATE_SSL));
        defaultProperties.setProperty(PROPERTIES_PATH_KEY, PROPERTIES_PATH);
        defaultProperties.setProperty(LOGLEVEL_KEY, LOGLEVEL);
        configProperties = new Properties(defaultProperties);
        loadConfigFromFile(PROPERTIES_PATH);
        setupLogging(getLoglevel());
    }

    private static void setupLogging(Level level) {
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        logger.addHandler(handler);
    }

    public static String getServerHost() {
        return configProperties.getProperty(THIS_HOST_KEY);
    }

    public static void setServerHost(String serverHost) {
        setConfigProperty(THIS_HOST_KEY, serverHost);
    }

    public static int getServerPort() {
        return Integer.parseInt(configProperties.getProperty(THIS_PORT_KEY));
    }

    public static void setServerPort(int serverPort) {
        setConfigProperty(THIS_PORT_KEY, Integer.toString(serverPort));
    }

    public static String getRemoteHost() {
        return configProperties.getProperty(REMOTE_HOST_KEY);
    }

    public static void setRemoteHost(String remoteHost) {
        setConfigProperty(REMOTE_HOST_KEY, remoteHost);
    }

    public static int getRemotePort() {
        return Integer.parseInt(configProperties.getProperty(REMOTE_PORT_KEY));
    }

    public static void setRemotePort(int port) {
        setConfigProperty(REMOTE_PORT_KEY, Integer.toString(port));
    }

    public static int getServerBacklog() {
        return Integer.parseInt(configProperties.getProperty(SERVER_BACKLOG_KEY));
    }

    public static void setServerBacklog(int serverBacklog) {
        setConfigProperty(SERVER_BACKLOG_KEY, Integer.toString(serverBacklog));
    }

    public static boolean getSSLTermination()
    {
        return Boolean.parseBoolean(configProperties.getProperty(TERMINATE_SSL_KEY));
    }

    public static void setSSLTermination(boolean f)
    {
        setConfigProperty(TERMINATE_SSL_KEY, Boolean.toString(f));
    }

    public static boolean getImplicitSSL()
    {
        return Boolean.parseBoolean(configProperties.getProperty(IMPLICIT_SSL_KEY));
    }

    public static void setImplicitSSL(boolean f)
    {
        setConfigProperty(IMPLICIT_SSL_KEY, Boolean.toString(f));
    }

    public static Level getLoglevel()
    {
        try {
            return Level.parse(configProperties.getProperty(LOGLEVEL_KEY));
        } catch (Exception e) {
            System.err.println("unknown log level");
            return Level.INFO;
        }
    }

    private static void setConfigProperty(String key, String value) {
        configProperties.setProperty(key, value);
    }

    public static boolean loadConfigFromFile(String filename) {
        File f = new File(filename);
        if (!f.exists() || !f.canRead()) {
            System.err.println("Unable to access config file: " + filename);
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

    public static void log(Level level, String msg) {
        logger.log(level, msg);
    }

    public static void logSevere(String msg) {
        logger.log(Level.SEVERE, msg);
    }

    public static void logWarning(String msg) {
        logger.log(Level.WARNING, msg);
    }

    public static void logInfo(String msg) {
        logger.log(Level.INFO, msg);
    }

    public static void logConfig(String msg) {
        logger.log(Level.CONFIG, msg);
    }

    public static void logFine(String msg) {
        logger.log(Level.FINE, msg);
    }

    public static void logFiner(String msg) {
        logger.log(Level.FINER, msg);
    }

    public static void logFinest(String msg) {
        logger.log(Level.FINEST, msg);
    }
}
