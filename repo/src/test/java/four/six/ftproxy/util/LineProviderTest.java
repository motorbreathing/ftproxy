package four.six.ftproxy.util;

import static org.junit.Assert.assertTrue;

import junit.framework.*;
import org.junit.Test;

public class LineProviderTest {
    @Test
    public void doTest1() {
        LineProvider lp = new LineProvider();
        assertTrue(lp.getLine() == null);

        lp.add("abcd");
        assertTrue(lp.getLine() == null);

        assertTrue(lp.getStashedString().equals("abcd"));

        assertTrue(lp.getLine() == null);

        lp.add("\n");
        assertTrue(lp.getLine().equals("abcd"));
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString() == null);

        lp.add("a\r\n");
        assertTrue(lp.getStashedString().length() == 3);
        assertTrue(lp.getLine().equals("a"));
        assertTrue(lp.getStashedString() == null);

        lp.add("\r\n");
        assertTrue(lp.getStashedString().length() == 2);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString() == null);

        lp.add("\n\n\n");
        assertTrue(lp.getStashedString().length() == 3);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString().length() == 2);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString().length() == 1);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString() == null);

        lp.add("\r\n\r\n\r\n");
        assertTrue(lp.getStashedString().length() == 6);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString().length() == 4);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString().length() == 2);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString() == null);

        lp.add("\r\n\r\n\r\nlast");
        assertTrue(lp.getStashedString().length() == 10);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString().length() == 8);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString().length() == 6);
        assertTrue(lp.getLine() == null);
        assertTrue(lp.getStashedString().equals("last"));
        lp.add("\r\n");
        assertTrue(lp.getLine().equals("last"));
        assertTrue(lp.getStashedString() == null);
    }
}
