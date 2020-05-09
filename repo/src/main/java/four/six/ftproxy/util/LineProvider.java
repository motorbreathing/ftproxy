package four.six.ftproxy.util;

import io.netty.buffer.ByteBuf;

public class LineProvider {
    private String stash = Util.EMPTYSTRING;

    public LineProvider() {
        add("");
    }

    public LineProvider(String s) {
        add(s);
    }

    public LineProvider(ByteBuf b) {
        add(b.toString(Util.UTF8charset));
    }

    public void add(String s) {
        stash += s;
    }

    public void clearStashedString() {
        stash = Util.EMPTYSTRING;
    }

    public String getStashedString() {
        return stash.length() == 0 ? null : stash;
    }

    public String getLine() {
        // No stashed data?
        if (stash.length() == 0)
            return null;

        int index = stash.indexOf(Util.LF);
        // Have stashed data, but yet to see a newline
        if (index < 0)
            return null;

        String result = Util.EMPTYSTRING;
        if (index > 0) {
            // Grab everything that precedes the newline
            result = stash.substring(0, index);
            if (result.charAt(result.length() - 1) == Util.CR) {
                // All we had before the newline was a carriage return!
                if (result.length() == 1)
                    result = Util.EMPTYSTRING;
                else // Lose the carriage return as well
                    result = result.substring(0, result.length() - 1);
            }
        }

        // Do we have data past the newline?
        if (index + 1 < stash.length()) {
            // Yes, so move the 'read pointer' accordingly
            stash = stash.substring(index + 1);
        } else {
            // We have consumed all that was stashed
            stash = Util.EMPTYSTRING;
        }

        if (result.length() > 0)
            return result;

        return null;
    }
}
