package com.voxai.core.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class AesUtils {


    //参数分别代表 算法名称/加密模式/数据填充方式
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * 加密
     *
     * @param data
     * @param password 推荐使用16位
     * @return
     */
    public static String encrypt(String data, String password) throws Exception {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        SecretKeySpec secretKeySpec = new SecretKeySpec(password.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * 解密
     *
     * @param decryptCode
     * @param password
     * @return
     */
    public static String decrypt(String decryptCode, String password) throws Exception {
        if (StringUtils.isBlank(decryptCode)) {
            return null;
        }
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        SecretKeySpec secretKeySpec = new SecretKeySpec(password.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] encryptByte = Base64.decodeBase64(decryptCode);
        byte[] original = cipher.doFinal(encryptByte);
        return new String(original);
    }
}
