package com.voxai.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AesUtilsTest {

    private static final String PASSWORD = "1234567890123456";

    @Test
    void shouldEncryptAndDecrypt() throws Exception {
        String original = "Hello World";
        String encrypted = AesUtils.encrypt(original, PASSWORD);
        assertNotNull(encrypted);
        assertNotEquals(original, encrypted);
        String decrypted = AesUtils.decrypt(encrypted, PASSWORD);
        assertEquals(original, decrypted);
    }

    @Test
    void shouldReturnNullForBlankInput() throws Exception {
        assertNull(AesUtils.encrypt(null, PASSWORD));
        assertNull(AesUtils.encrypt("", PASSWORD));
        assertNull(AesUtils.decrypt(null, PASSWORD));
        assertNull(AesUtils.decrypt("", PASSWORD));
    }

    @Test
    void shouldProduceDifferentResultsForDifferentPasswords() throws Exception {
        String data = "test";
        String encrypted1 = AesUtils.encrypt(data, "key1_12345678901");
        String encrypted2 = AesUtils.encrypt(data, "key2_12345678902");
        assertNotEquals(encrypted1, encrypted2);
    }
}
