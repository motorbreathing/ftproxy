package four.six.ftproxy.util;

public class TestUtil {
    public static String withoutCRLF(String original) {
        return original.substring(0, original.indexOf('\r'));
    }
}
