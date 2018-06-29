package com.github.lovasoa.replacement;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class ReplacementDecoder extends CharsetDecoder {
    private boolean replacementErrorReturned = false;

    ReplacementDecoder() {
        super(ReplacementCharset.REPLACEMENT_CHARSET, Float.MIN_VALUE, 1);
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        if (in.hasRemaining() && !replacementErrorReturned) {
            replacementErrorReturned = true;
            return CoderResult.malformedForLength(in.remaining());
        }
        return CoderResult.UNDERFLOW;
    }
}
