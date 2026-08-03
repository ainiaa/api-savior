package cn.gudqs7.plugins.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 加密
 *
 * @author hutao
 */
@Slf4j
public class EncryptUtils {

    private EncryptUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String encryptByPublicKey(String content, String pathPublicKey) throws Exception {
        PublicKey publicKey = getPublicKey(pathPublicKey);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // 准备公钥加密
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);


        byte[] bytes = content.getBytes();
        int inputLen = bytes.length;
        //偏移量
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > 117) {
                cache = cipher.doFinal(bytes, offLen, 117);
            } else {
                cache = cipher.doFinal(bytes, offLen, inputLen - offLen);
            }
            bops.write(cache);
            i++;
            offLen = 117 * i;
        }
        bops.close();
        byte[] encryptedData = bops.toByteArray();
        String cipherText = Base64.encodeBase64String(encryptedData);
        log.info("encryptByPublicKey 密文:" + cipherText);
        return cipherText;
    }


    public static String encryptByPrivateKey(String content, String pathPrivateKey) throws Exception {
        PrivateKey privateKey = getPrivateKey(pathPrivateKey);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // 准备公钥加密
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);


        byte[] bytes = content.getBytes();
        int inputLen = bytes.length;
        int offLen = 0;//偏移量
        int i = 0;
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > 117) {
                cache = cipher.doFinal(bytes, offLen, 117);
            } else {
                cache = cipher.doFinal(bytes, offLen, inputLen - offLen);
            }
            bops.write(cache);
            i++;
            offLen = 117 * i;
        }
        bops.close();
        byte[] encryptedData = bops.toByteArray();
        String cipherText = Base64.encodeBase64String(encryptedData);
        log.info("encryptByPublicKey 密文:" + cipherText);
        return cipherText;
    }

    public static String decryptByPrivateKey(String content, String pathPrivateKey) throws Exception {
        PrivateKey privateKey = getPrivateKey(pathPrivateKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // 准备私钥解密
        cipher.init(Cipher.DECRYPT_MODE, privateKey);


        byte[] bytes = java.util.Base64.getDecoder().decode(content);
        int inputLen = bytes.length;
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > 128) {
                cache = cipher.doFinal(bytes, offLen, 128);
            } else {
                cache = cipher.doFinal(bytes, offLen, inputLen - offLen);
            }
            byteArrayOutputStream.write(cache);
            i++;
            offLen = 128 * i;

        }
        byteArrayOutputStream.close();
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        log.info("decryptByPrivateKey 解密：" + new String(byteArray));
        return new String(byteArray);
    }


    public static String decryptByPublicKey(String content, String pathPublicKey) throws Exception {
        PublicKey publicKey = getPublicKey(pathPublicKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // 准备私钥解密
        cipher.init(Cipher.DECRYPT_MODE, publicKey);


        byte[] bytes = java.util.Base64.getDecoder().decode(content);
        int inputLen = bytes.length;
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > 128) {
                cache = cipher.doFinal(bytes, offLen, 128);
            } else {
                cache = cipher.doFinal(bytes, offLen, inputLen - offLen);
            }
            byteArrayOutputStream.write(cache);
            i++;
            offLen = 128 * i;

        }
        byteArrayOutputStream.close();
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        log.info("decryptByPrivateKey 解密：" + new String(byteArray));
        return new String(byteArray);
    }


    /**
     * 读取base64编码的公钥文件并构造 PKCS#8 格式的私钥
     *
     * @return PublicKey
     */
    public static PrivateKey getPrivateKey(String pathPrivateKey) throws Exception {
        String text = pathPrivateKey.replaceAll("[\r\n]", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
        byte[] data = Base64.decodeBase64(text);
        PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(data);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(pkcs8);
    }

    /**
     * 读取base64编码的公钥文件并构造X509EncodedKeySpec格式的公钥
     *
     * @return PublicKey
     */
    public static PublicKey getPublicKey(String pathPublicKey) throws Exception {
        String text = pathPublicKey.replaceAll("[\r\n]", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        byte[] data = Base64.decodeBase64(text);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(data);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(x509);
    }
}
