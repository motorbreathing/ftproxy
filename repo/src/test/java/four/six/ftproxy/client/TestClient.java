package four.six.ftproxy.client;


import four.six.ftproxy.ftp.FTPDataRelayCommand;
import four.six.ftproxy.ftp.FTPUtil;
import four.six.ftproxy.netty.NettyUtil;
import four.six.ftproxy.netty.StringDecoder;
import four.six.ftproxy.netty.StringEncoder;
import four.six.ftproxy.netty.TestClientHandler;
import four.six.ftproxy.ssl.SSLHandlerProvider;
import four.six.ftproxy.util.LineProvider;
import four.six.ftproxy.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestClient {
    private Channel ch;

    private LineProvider lp;
    private Lock lpLock;
    private Condition lpCond;

    private String fileContents = Util.EMPTYSTRING;
    private Lock fileLock;
    private Condition fileCond;

    private boolean dataSSLEnabled = false;
    private int readTimeoutMillis = 5000;

    public TestClient() {
        lp = new LineProvider();
        lpLock = new ReentrantLock();
        lpCond = lpLock.newCondition();

        fileLock = new ReentrantLock();
        fileCond = fileLock.newCondition();
    }

    public void addString(String data) {
        lpLock.lock();
        lp.add(data);
        lpCond.signalAll();
        lpLock.unlock();
    }

    public String readLine(long millis) {
        try {
            lpLock.lock();
            String res = lp.getLine();
            if (res != null) return res;
            lpCond.await(millis, TimeUnit.MILLISECONDS);
            return lp.getLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lpLock.unlock();
        }
        return null;
    }

    public String readFile(long millis) {
        try {
            fileLock.lock();
            if (fileContents.length() != 0) return fileContents;
            fileCond.await(millis, TimeUnit.MILLISECONDS);
            return fileContents;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
        return null;
    }

    private ChannelInitializer<? extends Channel> getChannelInitializer(boolean ssl) {
        TestClientHandler tch =
                new TestClientHandler() {
                    @Override
                    public void channelRead0(ChannelHandlerContext ctx, String incoming) throws Exception {
                        addString(incoming);
                    }
                };

        if (ssl) // SSL enabled
        return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(
                                    SSLHandlerProvider.getClientSSLHandler(ch),
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    tch);
                }
            };
        else // SSL disabled
        return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), tch);
                }
            };
    }

    public void write(String line) throws Exception {
        ch.writeAndFlush(line).sync();
    }

    public void requestExplicitSSL() throws Exception {
        write("AUTH TLS\r\n");
    }

    public void requestDataSSL() throws Exception {
        write("PROT P\r\n");
    }

    public void requestDataClear() throws Exception {
        write("PROT C\r\n");
    }

    public void connect(String host, int port, boolean ssl) throws Exception {
        try {
            Util.logInfo("Client: attempting to connect to " + host + " at port " + port);
            ch = NettyUtil.getChannelToHost(host, port, getChannelInitializer(ssl)).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void resetFileContents() {
        fileContents = Util.EMPTYSTRING;
    }

    public void addFileContent(String incoming) {
        fileLock.lock();
        fileContents += incoming;
        fileLock.unlock();
    }

    public void finishFileContent() {
        fileLock.lock();
        fileCond.signalAll();
        fileLock.unlock();
    }

    protected ChannelHandler getFileReceiveHandler() {
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                String incoming = ((ByteBuf) msg).toString(Charset.forName("UTF-8"));
                Util.logFine("getFileActiveV4 handler: " + incoming);
                TestClient.this.addFileContent(incoming);
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {
                TestClient.this.finishFileContent();
            }
        };
    }

    protected ChannelInitializer<SocketChannel> getFileReceiveInitializer() {
        ChannelInitializer<SocketChannel> ci =
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        Util.logFinest("File receive: initializing channel");
                        if (dataSSLEnabled) {
                            ch.pipeline().addFirst(SSLHandlerProvider.getClientSSLHandler(ch));
                            Util.logFine("File receive: enabling SSL");
                        }
                        ChannelHandler handler = TestClient.this.getFileReceiveHandler();
                        ch.pipeline().addLast(handler);
                    }
                };
        return ci;
    }

    public String getFilePassiveV4() throws Exception {
        if (ch == null)
            throw new IllegalStateException();
        InetAddress addr = ((SocketChannel) ch).localAddress().getAddress();
        if (addr.getAddress().length != Util.IPV4_ADDRESS_LENGTH) {
            Util.logWarning("Invalid address (length = " + addr.getAddress().length + ") for Active V4 relay");
            throw new IllegalStateException();
        }
        write("PASV\r\n");
        String c = readLine(readTimeoutMillis);
        if (c == null)
            return null;
        c = c.substring(c.indexOf(Util.LEFT_PARA) + 1, c.indexOf(Util.RIGHT_PARA));
        resetFileContents();
        InetSocketAddress address = FTPUtil.processCommaDelimitedV4SocketAddress(c);
        NettyUtil.getChannelToAddress(address, getFileReceiveInitializer());
        return readFile(readTimeoutMillis);
    }

    public String getFilePassiveV6() throws Exception {
        if (ch == null)
            throw new IllegalStateException();
        InetAddress addr = ((SocketChannel) ch).localAddress().getAddress();
        if (addr.getAddress().length != Util.IPV6_ADDRESS_LENGTH) {
            Util.logWarning("Invalid address (length = " + addr.getAddress().length + ") for Active V6 relay");
            throw new IllegalStateException();
        }
        write("EPSV\r\n");
        String c = readLine(readTimeoutMillis);
        if (c == null)
            return null;
        c = c.substring(c.indexOf(Util.LEFT_PARA) + 1, c.indexOf(Util.RIGHT_PARA));
        Util.logFine("EPSV response is: " + c);
        resetFileContents();
        int port = FTPUtil.processPipeDelimitedV6SocketAddress(c);
        NettyUtil.getChannelToAddress(new InetSocketAddress(addr, port), getFileReceiveInitializer());
        return readFile(readTimeoutMillis);
    }

    public String getFileActiveV4() throws Exception {
        if (ch == null)
            throw new IllegalStateException();
        InetAddress addr = ((SocketChannel) ch).localAddress().getAddress();
        if (addr.getAddress().length != Util.IPV4_ADDRESS_LENGTH) {
            Util.logWarning("Invalid address (length = " + addr.getAddress().length + ")for Active V4 relay");
            throw new IllegalStateException();
        }
        ChannelFuture cf = NettyUtil.getListenerChannel(addr, getFileReceiveInitializer());
        InetSocketAddress localAddr = (InetSocketAddress) cf.channel().localAddress();
        resetFileContents();
        write(FTPDataRelayCommand.getRelayCommand(localAddr));
        String res = readFile(readTimeoutMillis);
        cf.channel().close();
        return res;
    }

    public String getFileActiveV6() throws Exception {
        if (ch == null)
            throw new IllegalStateException();
        InetAddress addr = ((SocketChannel) ch).localAddress().getAddress();
        if (addr.getAddress().length != Util.IPV6_ADDRESS_LENGTH) {
            Util.logWarning("Invalid address (length = " + addr.getAddress().length + ") for Active V6 relay");
            throw new IllegalStateException();
        }
        ChannelFuture cf = NettyUtil.getListenerChannel(addr, getFileReceiveInitializer());
        InetSocketAddress localAddr = (InetSocketAddress) cf.channel().localAddress();
        resetFileContents();
        write(FTPDataRelayCommand.getRelayCommand(localAddr));
        String res = readFile(readTimeoutMillis);
        cf.channel().close();
        return res;
    }

    public void connect(String host, int port) throws Exception {
        connect(host, port, false);
    }

    public void disconnect() throws Exception {
        Util.logFinest("Client: disconnecting");
        ch.close();
    }

    public void enableDataSSL() {
        dataSSLEnabled = true;
    }

    public void disableDataSSL() {
        dataSSLEnabled = false;
    }

    public void enableSSL() {
        if (ch != null)
            ch.pipeline().addFirst(SSLHandlerProvider.getClientSSLHandler(ch));
        else
            Util.logWarning("enableSSL() failed for client");
    }
}
