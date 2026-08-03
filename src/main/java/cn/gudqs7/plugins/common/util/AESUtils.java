package cn.gudqs7.plugins.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * @description
 */
public class AESUtils {
    private static final String ALGORITHM = "AES";
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final byte[] IV = new byte[12];

    public AESUtils() {
    }

    public static String encrypt(String pText, String key) throws GeneralSecurityException {
        byte[] plainByte = pText.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gSpec = new GCMParameterSpec(128, IV);
        cipher.init(1, getSecretKey(key), gSpec);
        return Base64.encodeBase64String(cipher.doFinal(plainByte));
    }

    public static String decrypt(String cText, String key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gSpec = new GCMParameterSpec(128, IV);
        cipher.init(2, getSecretKey(key), gSpec);
        byte[] plainText = cipher.doFinal(Base64.decodeBase64(cText));
        return new String(plainText, StandardCharsets.UTF_8);
    }

    private static SecretKey getSecretKey(String key) {
        byte[] secret = new byte[16];
        byte[] secretBytes = key.getBytes(StandardCharsets.UTF_8);
        int length = Math.min(secret.length, secretBytes.length);
        System.arraycopy(secretBytes, 0, secret, 0, length);
        return new SecretKeySpec(secret, "AES");
    }
}
