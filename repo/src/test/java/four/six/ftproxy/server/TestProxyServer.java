package four.six.ftproxy.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

import four.six.ftproxy.ftp.FTPChannelInitializer;
import four.six.ftproxy.ftp.FTPRelayHandler;

public class TestProxyServer extends AbstractTestServer {

    private final static String myName = "Test Proxy Server";
    private FTPChannelInitializer ftpChannelInitializer = null;
    private FTPRelayHandler handler = null;
    
    public TestProxyServer()
    {
        setMyName(myName);
    }

    @Override
    protected ChannelInitializer<? extends Channel> getTestServerChannelInitializer()
    {
        // If we have an already-setup ftp channel initializer...
        if (ftpChannelInitializer != null)
            return ftpChannelInitializer;

        ftpChannelInitializer = new FTPChannelInitializer(sslStatus);
        handler = (FTPRelayHandler)ftpChannelInitializer.getProtocolHandler();
        return ftpChannelInitializer;
    }

    private FTPRelayHandler getHandler()
    {
        if (handler != null)
            return handler;
        if (ftpChannelInitializer == null)
            ftpChannelInitializer = new FTPChannelInitializer(sslStatus);
        return (FTPRelayHandler)ftpChannelInitializer.getProtocolHandler();
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
