package four.six.ftproxy.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.ftp.FTProxyServerChannelInitializer;
import four.six.ftproxy.ftp.FTPRelayHandler;

public class TestProxyServer extends AbstractTestServer {

    private final static String myName = "Test Proxy Server";
    private FTProxyServerChannelInitializer ftpServerChannelInitializer = null;
    private FTPRelayHandler handler = null;
    
    public TestProxyServer()
    {
        setMyName(myName);
    }

    @Override
    protected ChannelInitializer<? extends Channel> getTestServerChannelInitializer()
    {
        // If we have an already-setup ftp channel initializer...
        if (ftpServerChannelInitializer != null)
            return ftpServerChannelInitializer;

        ftpServerChannelInitializer = new FTProxyServerChannelInitializer(sslStatus);
        handler = (FTPRelayHandler)ftpServerChannelInitializer.getProtocolHandler();
        return ftpServerChannelInitializer;
    }

    private FTPRelayHandler getHandler()
    {
        if (handler != null)
            return handler;
        if (ftpServerChannelInitializer == null)
            ftpServerChannelInitializer = new FTProxyServerChannelInitializer(sslStatus);
        return (FTPRelayHandler)ftpServerChannelInitializer.getProtocolHandler();
    }

    public void enableClientSSL()
    {
        getHandler().enableClientSSL();
    }

    // This is a proxy; enable SSL to the backend server
    public void enableServerSSL()
    {
        getHandler().enableServerSSL();
    }
}
