package four.six.ftproxy.netty;

import io.netty.channel.ChannelHandler;

public class TextRelayChannelInitializer extends AbstractChannelInitializer {
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    @Override
    public ChannelHandler getDecoder() {
        return DECODER;
    }

    @Override
    public ChannelHandler getEncoder() {
        return ENCODER;
    }

    @Override
    public ChannelHandler getProtocolHandler() {
        return new TextRelayHandler();
    }
}
