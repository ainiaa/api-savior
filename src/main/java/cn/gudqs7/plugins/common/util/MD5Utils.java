package cn.gudqs7.plugins.common.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5Utils
 *
 * @author Jeff.Liu
 * @date 2022-12-05
 **/
public class MD5Utils {
    private static final int SIGNUM = 1;
    private static final int HEX_FLAG = 16;
    private static final int SIGN_LENGTH = 32;
    private static final String FILL_CHAR = "0";

    public MD5Utils() {
    }

    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(Charset.forName("UTF-8")));
            BigInteger number = new BigInteger(1, messageDigest);

            String hashtext;
            for(hashtext = number.toString(16); hashtext.length() < 32; hashtext = "0" + hashtext) {
            }

            return hashtext.toUpperCase();
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
        }
    }
}
