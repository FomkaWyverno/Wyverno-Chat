package ua.wyverno.util.dropbox.hasher;

public class HexUtils {
    static final char[] HEX_DIGITS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static String hex(byte[] data)
    {
        char[] buf = new char[2*data.length];
        int i = 0;
        for (byte b : data) {
            buf[i++] = HEX_DIGITS[(b & 0xf0) >>> 4];
            buf[i++] = HEX_DIGITS[b & 0x0f];
        }
        return new String(buf);
    }
}
