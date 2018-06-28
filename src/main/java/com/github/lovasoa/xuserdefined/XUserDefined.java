package com.github.lovasoa.xuserdefined;

import java.nio.charset.*;

public class XUserDefined extends Charset {

    public static final Charset X_USER_DEFINED = new XUserDefined();
    private static final String CANONICAL_NAME = "x-user-defined";
    private static final String[] ALIASES = new String[]{};

    private XUserDefined() {
        super(CANONICAL_NAME, ALIASES);
    }

    @Override
    public boolean contains(Charset cs) {
        return cs.equals(StandardCharsets.US_ASCII);
    }

    public CharsetDecoder newDecoder() {
        return new XUserDefinedDecoder();
    }

    public CharsetEncoder newEncoder() {
        return new XUserDefinedEncoder();
    }

    public static char decode(byte b) {
        return (char) ((b >= 0) ? b : 0xF700 + (b & 0xFF));
    }

    public static byte encode(char c) {
        return (byte) (c & 0xFF);
    }
}
