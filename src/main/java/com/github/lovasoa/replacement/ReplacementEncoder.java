package com.github.lovasoa.replacement;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import static java.nio.charset.CodingErrorAction.REPLACE;

/**
 * Characters to bytes encoder for the x-user-defined charset.
 * <p>
 * By default, if the coding error action is REPLACE and no custom replacement has been set,
 * invalid (non x-user-defined) characters are encoded as the lower byte of their unicode code point.
 * This allows the encoder to work slightly faster. Otherwise, ff a custom replacement is set, it
 * will be respected.
 */
public class ReplacementEncoder extends CharsetEncoder {

    private boolean hasCustomReplacement = false;

    ReplacementEncoder() {
        super(ReplacementCharset.X_USER_DEFINED, 1, 1);
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        boolean fast = !hasCustomReplacement && unmappableCharacterAction() == REPLACE;
        return fast ? fastEncodeLoop(in, out) : slowEncodeLoop(in, out);
    }

    private CoderResult fastEncodeLoop(CharBuffer in, ByteBuffer out) {
        for (; ; ) {
            if (!in.hasRemaining()) return CoderResult.UNDERFLOW;
            if (!out.hasRemaining()) return CoderResult.OVERFLOW;
            out.put((byte) (in.get() & 0xFF));
        }
    }

    private CoderResult slowEncodeLoop(CharBuffer in, ByteBuffer out) {
        for (; ; ) {
            if (!in.hasRemaining()) return CoderResult.UNDERFLOW;
            if (!out.hasRemaining()) return CoderResult.OVERFLOW;
            char c = in.get();
            if (canEncode(c)) out.put(ReplacementCharset.encode(c));
            else {
                in.position(in.position() - 1); // unread c
                return CoderResult.unmappableForLength(1);
            }
        }
    }


    @Override
    public boolean canEncode(char c) {
        return c <= '\u007F' || ('\uF780' <= c && c <= '\uF7FF');
    }

    @Override
    public boolean canEncode(CharSequence cs) {
        for (int i = 0; i < cs.length(); i++) {
            if (!canEncode(cs.charAt(i))) return false;
        }
        return true;
    }

    @Override
    public boolean isLegalReplacement(byte[] r) {
        // The decoder can decode any sequence of bytes
        return r != null && r.length > 0;
    }

    @Override
    protected void implReplaceWith(byte[] newReplacement) {
        hasCustomReplacement = true;
    }
}
