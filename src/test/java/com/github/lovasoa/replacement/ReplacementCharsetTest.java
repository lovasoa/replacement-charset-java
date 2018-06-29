package com.github.lovasoa.replacement;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

import static java.nio.charset.CodingErrorAction.IGNORE;
import static java.nio.charset.CodingErrorAction.REPLACE;
import static org.junit.jupiter.api.Assertions.*;

class ReplacementCharsetTest {
    private final Charset charset = ReplacementCharset.X_USER_DEFINED;

    @Test
    void testAscii() {
        assertEncodeDecode("abc!", new byte[]{'a', 'b', 'c', '!'});
    }

    @Test
    void testNonPrintable() {
        assertEncodeDecode("\u0000\u0001\u0002\u0003", new byte[]{0, 1, 2, 3});
    }

    @Test
    void testPrivateUseArea() {
        assertEncodeDecode(
                "\uF780\uF790\uF7F0\uF7FF",
                new byte[]{(byte) 0x80, (byte) 0x90, (byte) 0xF0, (byte) 0xFF}
        );
    }

    @Test
    void testAllBytes() {
        byte[] allBytes = new byte[1 << Byte.SIZE];
        for (int i = 0; i < allBytes.length; i++) allBytes[i] = (byte) i;
        String decoded = new String(allBytes, charset);
        byte[] reencoded = decoded.getBytes(charset);
        assertArrayEquals(allBytes, reencoded);
    }

    @Test
    void testUnmappableDefault() {
        // By default, we use the lower byte of the unicode codepoint for all characters
        assertEncode("a\uF77F\uffff", new byte[]{'a', (byte) 0x7F, (byte) 0xFF});
    }

    @Test
    void testUnmappableIgnore() {
        // Ignore (skip) invalid code points
        CharsetEncoder encoder = charset.newEncoder().onUnmappableCharacter(IGNORE);
        assertEncode("a\uF77F\uffffb\uABCD", new byte[]{'a', 'b'}, encoder);
    }

    @Test
    void testUnmappableCustomReplace() {
        // Ignore (skip) invalid code points
        CharsetEncoder encoder = charset.newEncoder()
                .onUnmappableCharacter(REPLACE)
                .replaceWith(new byte[]{0x66});
        assertEncode("héé", new byte[]{'h', (byte) 0x66, (byte) 0x66}, encoder);
    }

    @Test
    void testContains() {
        assertTrue(charset.contains(StandardCharsets.US_ASCII));
        assertFalse(charset.contains(StandardCharsets.UTF_8));
    }

    @Test
    void testCanEncode() {
        assertTrue(charset.newEncoder().canEncode("hello"));
        assertTrue(charset.newEncoder().canEncode("\0\f"));
        assertTrue(charset.newEncoder().canEncode("\uF780\uF7FF"));

        assertFalse(charset.newEncoder().canEncode("héhé"));
        assertFalse(charset.newEncoder().canEncode("\uF77F"));
    }

    @Test
    void testProvider() {
        assertEquals(charset, Charset.forName(charset.name()));
        assertEquals(charset, Charset.forName(charset.name().toUpperCase()));
        assertTrue(Charset.availableCharsets().values().contains(charset));
        // Ensure our provider did not mess up with existing ones
        assertEquals(StandardCharsets.UTF_8, Charset.forName("UTF-8"));
        assertNotEquals(charset, Charset.forName("x-maccyrillic"));
    }

    private void assertEncodeDecode(String str, byte[] bts) {
        assertEncode(str, bts);
        assertDecode(str, bts);
    }

    private void assertEncode(String str, byte[] bts, CharsetEncoder encoder) {
        try {
            CharBuffer charBuffer = CharBuffer.wrap(str);
            ByteBuffer byteBuffer = encoder.encode(charBuffer);
            assertEquals(ByteBuffer.wrap(bts), byteBuffer);
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertEncode(String str, byte[] bts) {
        final CharsetEncoder encoder = charset
                .newEncoder()
                .onUnmappableCharacter(REPLACE)
                .onMalformedInput(REPLACE);
        assertEncode(str, bts, encoder);
    }

    private void assertDecode(String str, byte[] bts) {
        String decodedBts = new String(bts, charset);
        assertEquals(str, decodedBts);
    }
}