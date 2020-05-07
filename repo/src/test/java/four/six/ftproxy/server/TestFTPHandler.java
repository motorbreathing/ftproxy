package four.six.ftproxy.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import four.six.ftproxy.ssl.SSLHandlerProvider;
import four.six.ftproxy.util.Util;
import four.six.ftproxy.ftp.FTPUtil;
import four.six.ftproxy.ftp.FTPAuthCommand;
import four.six.ftproxy.ftp.FTPProtCommand;

public class TestFTPHandler extends TestEchoHandler {

    public static final String GENERIC_RESPONSE_200_STR = "200 Generic Approval\r\n";
    public static final String PROT_RESPONSE_200_C_STR = "200 Protection level set to C\r\n";
    public static final String PROT_RESPONSE_200_P_STR = "200 Protection level set to P\r\n";

    private ChannelHandlerContext ctx;
    boolean sslAllowed = true;

    protected void process(ChannelHandlerContext ctx, String incoming) {
        String[] args = incoming.split(Util.REGEX_SPLIT_BY_SPACES);
        if (args[0].equalsIgnoreCase(FTPAuthCommand.COMMAND_STR)) {
            if (args[1].equalsIgnoreCase("SSL") || args[1].equalsIgnoreCase("TLS")) {
                if (sslAllowed) {
                    ctx.writeAndFlush(FTPAuthCommand.RESPONSE_STR);
                    enableExplicitSSL();
                } else {
                    ctx.writeAndFlush(FTPUtil.UNIMPLEMENTED_COMMAND_STR);
                }

            } else {
                ctx.writeAndFlush(FTPUtil.UNRECOGNIZED_COMMAND_STR);
            }
            return;
        }

        if (args[0].equalsIgnoreCase(FTPProtCommand.COMMAND_STR)) {
            if (args[1].equalsIgnoreCase("P")) {
                ctx.writeAndFlush(PROT_RESPONSE_200_P_STR);
            } else {
                ctx.writeAndFlush(PROT_RESPONSE_200_C_STR);
            }
            return;
        }

        ctx.writeAndFlush(GENERIC_RESPONSE_200_STR);
    }
}
