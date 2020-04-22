package four.six.ftproxy.util;

import junit.framework.*;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class LineProviderTest
{
    @Test
    public void doTest1()
    {
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
    }
}
