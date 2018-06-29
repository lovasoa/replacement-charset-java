package com.github.lovasoa.replacement;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class ReplacementDecoder extends CharsetDecoder {
    ReplacementDecoder() {
        super(ReplacementCharset.X_USER_DEFINED, 1, 1);
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        while (true) {
            if (!in.hasRemaining()) return CoderResult.UNDERFLOW;
            if (!out.hasRemaining()) return CoderResult.OVERFLOW;
            byte b = in.get();
            out.append(ReplacementCharset.decode(b));
        }
    }
}
