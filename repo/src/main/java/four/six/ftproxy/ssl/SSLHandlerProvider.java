package four.six.ftproxy.ssl;

import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.channel.socket.SocketChannel;

import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;

public class SSLHandlerProvider {
    private static SslContext serverSslContext;
    private static SslContext clientSslContext;

    static {
        try {
            // Server-side SSL
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            serverSslContext = SslContextBuilder
                               .forServer(ssc.certificate(), ssc.privateKey())
                               .build();
            // Client-side SSL
            clientSslContext = SslContextBuilder.forClient()
                               .trustManager(InsecureTrustManagerFactory.INSTANCE)
                               .build();
        } catch (CertificateException certex) {
            certex.printStackTrace();
        } catch (SSLException sslex) {
            sslex.printStackTrace();
        }
    }

    public static SslHandler getServerSSLHandler(SocketChannel ch){
        if (serverSslContext == null)
            return null;

        return serverSslContext.newHandler(ch.alloc());
    }

    public static SslHandler getclientSSLHandler(SocketChannel ch){
        if (clientSslContext == null)
            return null;

        return clientSslContext.newHandler(ch.alloc());
    }
}
