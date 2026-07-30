package com.voxai.api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BcryptUtilTest {

    @Test
    void shouldEncryptPassword() {
        String hashed = BcryptUtil.encrypt("password123");
        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$"));
    }

    @Test
    void shouldVerifyCorrectPassword() {
        String hashed = BcryptUtil.encrypt("password123");
        assertTrue(BcryptUtil.checkPwd("password123", hashed));
    }

    @Test
    void shouldRejectWrongPassword() {
        String hashed = BcryptUtil.encrypt("password123");
        assertFalse(BcryptUtil.checkPwd("wrong", hashed));
    }

    @Test
    void shouldProduceDifferentHashes() {
        String hash1 = BcryptUtil.encrypt("password123");
        String hash2 = BcryptUtil.encrypt("password123");
        assertNotEquals(hash1, hash2);
    }
}
