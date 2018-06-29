package com.github.lovasoa.replacement;

import java.nio.charset.*;

public class ReplacementCharset extends Charset {

    public static final Charset X_USER_DEFINED = new ReplacementCharset();
    private static final String CANONICAL_NAME = "replacement";
    private static final String[] ALIASES = new String[]{
            "csiso2022kr",
            "hz-gb-2312",
            "iso-2022-cn",
            "iso-2022-cn-ext",
            "iso-2022-kr"
    };

    private ReplacementCharset() {
        super(CANONICAL_NAME, ALIASES);
    }

    @Override
    public boolean contains(Charset cs) {
        return cs.equals(StandardCharsets.US_ASCII);
    }

    public CharsetDecoder newDecoder() {
        return new ReplacementDecoder();
    }

    public CharsetEncoder newEncoder() {
        return new ReplacementEncoder();
    }

    public static char decode(byte b) {
        return (char) ((b >= 0) ? b : 0xF700 + (b & 0xFF));
    }

    public static byte encode(char c) {
        return (byte) (c & 0xFF);
    }
}
