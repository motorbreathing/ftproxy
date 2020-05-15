package four.six.ftproxy.netty;

import four.six.ftproxy.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.util.List;

@Sharable
public class StringDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final Charset charset;

    /** Creates a new instance with the current system character set. */
    public StringDecoder() {
        this(Charset.defaultCharset());
    }

    /** Creates a new instance with the specified character set. */
    public StringDecoder(Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add(msg.toString(charset));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Util.logFinest("StringDecoder: removed");
    }
}
