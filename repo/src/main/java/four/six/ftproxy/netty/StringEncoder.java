package four.six.ftproxy.netty;

import four.six.ftproxy.util.Util;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@Sharable
public class StringEncoder extends MessageToMessageEncoder<CharSequence> {

    private final Charset charset;

    /** Creates a new instance with the current system character set. */
    public StringEncoder() {
        this(Charset.defaultCharset());
    }

    /** Creates a new instance with the specified character set. */
    public StringEncoder(Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out)
            throws Exception {
        if (msg.length() == 0) {
            return;
        }
        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), charset));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Util.log("StringEncoder: removed");
    }
}
