package four.six.ftproxy.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class Util
{
    private static final String DEFAULT_THIS_HOST_STR = "127.0.0.1";
    private static final String DEFAULT_THIS_PORT_STR = "8080";
    private static final String DEFAULT_REMOTE_HOST_STR = "127.0.0.1";
    private static final String DEFAULT_REMOTE_PORT_STR = "14646";
    private static final String THIS_HOST_KEY = "host";
    private static final String THIS_PORT_KEY = "port";
    private static final String REMOTE_HOST_KEY = "remote-host";
    private static final String REMOTE_PORT_KEY = "remote-port";

    public static final String THIS_HOST =
        System.getProperty(THIS_HOST_KEY, DEFAULT_THIS_HOST_STR);
    public static final int THIS_PORT =
        Integer.parseInt(System.getProperty(THIS_PORT_KEY, DEFAULT_THIS_PORT_STR));
    public static final String REMOTE_HOST =
        System.getProperty(REMOTE_HOST_KEY, DEFAULT_REMOTE_HOST_STR);
    public static final int REMOTE_PORT =
        Integer.parseInt(System.getProperty(REMOTE_PORT_KEY, DEFAULT_REMOTE_PORT_STR));
}
