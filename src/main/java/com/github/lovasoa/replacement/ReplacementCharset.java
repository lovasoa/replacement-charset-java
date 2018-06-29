package com.github.lovasoa.replacement;

import java.nio.charset.*;

public class ReplacementCharset extends Charset {
    private static final String CANONICAL_NAME = "replacement";
    private static final String[] ALIASES = new String[]{
            "csiso2022kr",
            "hz-gb-2312",
            "iso-2022-cn",
            "iso-2022-cn-ext",
            "iso-2022-kr"
    };
    public static final Charset REPLACEMENT_CHARSET = new ReplacementCharset();


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
        throw new UnsupportedOperationException("This charset does not support encoding");
    }
}
