package cn.gudqs7.plugins.common.util;

import java.util.Objects;

/**
 * StringUtils
 *
 * @author Jeff.Liu
 * @date 2022-10-26
 **/
public class StringUtils {

    private StringUtils() {
    }

    public static boolean equals(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    public static String getNonNullString(String str){
        return Objects.isNull(str) ? "" : str;
    }


}
