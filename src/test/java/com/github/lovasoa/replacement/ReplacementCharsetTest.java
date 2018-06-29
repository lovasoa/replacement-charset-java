package com.github.lovasoa.replacement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.*;

class ReplacementCharsetTest {
    private static final String REPLACEMENT_CHAR = "\ufffd";
    private static final Charset charset = ReplacementCharset.REPLACEMENT_CHARSET;

    @Test
    void testAscii() {
        assertDecodeToReplacement(new byte[]{'a', 'b', 'c', '!'});
    }

    @Test
    void testNonAscii() {
        assertDecodeToReplacement(new byte[]{-1, -127});
    }

    @Test
    void testAllBytes() {
        byte[] allBytes = new byte[1 << Byte.SIZE];
        for (int i = 0; i < allBytes.length; i++) allBytes[i] = (byte) i;
        String decoded = new String(allBytes, charset);
        assertEquals(REPLACEMENT_CHAR, decoded);
    }

    @Test
    void testProgressiveDecoding() {
        CharsetDecoder decoder = charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .replaceWith(REPLACEMENT_CHAR);

        CharBuffer out = CharBuffer.allocate(10);
        for (int i = 0; i < 1000; i++) {
            ByteBuffer in = ByteBuffer.wrap(new byte[]{(byte) i});
            decoder.decode(in, out, false);
        }
        decoder.decode(ByteBuffer.allocate(0), out, true);
        out.flip();
        CharBuffer expected = CharBuffer.wrap(REPLACEMENT_CHAR);
        assertEquals(expected, out);
    }

    @Test
    void testContains() {
        assertFalse(charset.contains(StandardCharsets.US_ASCII));
        assertFalse(charset.contains(StandardCharsets.UTF_8));
    }

    @Test
    void testProvider() {
        assertEquals(charset, Charset.forName(charset.name()));
        assertEquals(charset, Charset.forName(charset.name().toUpperCase()));
        // Ensure our provider did not mess up with existing ones
        assertEquals(StandardCharsets.UTF_8, Charset.forName("UTF-8"));
        assertNotEquals(charset, Charset.forName("x-maccyrillic"));
    }

    @Test
    void testAliases() {
        ReplacementCharsetProvider provider = new ReplacementCharsetProvider();
        SortedMap<String, Charset> available = Charset.availableCharsets();
        assertTrue(available.values().contains(charset));
        for (String alias : charset.aliases()) {
            assertEquals(charset, provider.charsetForName(alias));
        }
    }

    @Test
    void testNoEncoder() {
        assertThrows(UnsupportedOperationException.class, new Executable() {
            @Override
            public void execute() {
                charset.newEncoder();
            }
        });
    }

    private void assertDecodeToReplacement(byte[] bts) {
        String decodedBts = new String(bts, charset);
        assertEquals("\uFFFD", decodedBts);
    }
}