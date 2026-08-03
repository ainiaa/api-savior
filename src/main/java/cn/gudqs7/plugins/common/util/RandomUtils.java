package cn.gudqs7.plugins.common.util;

import java.security.SecureRandom;

/**
 * SerialNoUtils
 *
 * @author Jeff.Liu
 * @date 2021-08-18
 **/
public class RandomUtils {
    private RandomUtils(){}

    public static Integer randomInt(Integer min, Integer max) {
        SecureRandom random = new SecureRandom();

        return random.nextInt(max)%(max-min+1) + min;
    }
}
