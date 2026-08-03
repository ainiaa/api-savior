package cn.gudqs7.plugins.common.util;

import cn.gudqs7.plugins.common.enums.IntegerEnumInterface;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 常用比较方法
 *
 * @author Jeff.Liu
 * @date 2021-09-15
 */
public class CompareUtils {

    private CompareUtils() {
    }

    // region nullOrEqual
    public static boolean nullOrEqual(Integer a, Integer b) {
        return Objects.equals(a, b);
    }

    public static boolean nullOrEqual(Long a, Long b) {
        return Objects.equals(a, b);
    }

    public static boolean nullOrEqual(IntegerEnumInterface a, IntegerEnumInterface b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return areEqual(a.getCode(), b.getCode());
    }
    // endregion

    public static boolean nullOrEqual(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return areEqual(a, b);
    }

    // region notNullAndEqual
    public static boolean notNullAndEqual(Integer a, Integer b) {
        return a != null && a.equals(b);
    }

    public static boolean notNullAndEqual(Long a, Long b) {
        return a != null && a.equals(b);
    }
    // endregion

    public static boolean notNullAndEqual(IntegerEnumInterface a, IntegerEnumInterface b) {
        return a != null && areEqual(a.getCode(), b.getCode());
    }

    // region areEqual
    public static boolean areEqual(Integer a, int b) {
        return a != null && a == b;
    }

    public static boolean areEqual(Integer a, Integer b) {
        return a != null && a.equals(b);
    }

    public static boolean areEqual(int a, Integer b) {
        return areEqual(b, a);
    }

    public static boolean areEqual(Long a, long b) {
        return a != null && a == b;
    }

    public static boolean areEqual(long a, Long b) {
        return areEqual(b, a);
    }

    public static boolean areEqual(BigDecimal a, Integer b) {
        BigDecimal bb = null;
        if (b != null) {
            bb = BigDecimal.valueOf(b);
        }
        return areEqual(a, bb);
    }

    public static boolean areEqual(BigDecimal a, Long b) {
        BigDecimal bb = null;
        if (b != null) {
            bb = BigDecimal.valueOf(b);
        }
        return areEqual(a, bb);
    }

    public static boolean areEqual(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.compareTo(b) == 0;
    }

    public static boolean areEqual(IntegerEnumInterface namedEnum, Integer id) {
        return areEqual(id, namedEnum.getCode());
    }

    public static boolean areEqual(Integer id, IntegerEnumInterface namedEnum) {
        return areEqual(namedEnum, id);
    }
    // endregion

    public static boolean areEqual(String a, String b) {
        return StringUtils.equals(a, b);
    }

    // region notEqual
    public static boolean notEqual(Long a, long b) {
        return !areEqual(a, b);
    }

    public static boolean notEqual(long a, Long b) {
        return !areEqual(a, b);
    }

    public static boolean notEqual(IntegerEnumInterface namedEnum, Integer id) {
        return !areEqual(id, namedEnum.getCode());
    }

    public static boolean notEqual(Integer id, IntegerEnumInterface namedEnum) {
        return !areEqual(id, namedEnum.getCode());
    }

    public static boolean notEqual(BigDecimal a, Integer b) {
        return !areEqual(a, b);
    }

    public static boolean notEqual(BigDecimal a, Long b) {
        return !areEqual(a, b);
    }
    // endregion

    public static boolean notEqual(BigDecimal a, BigDecimal b) {
        return !areEqual(a, b);
    }

    // region greaterThan
    public static boolean greaterThan(int a, Integer b) {
        return b != null && a > b;
    }

    public static boolean greaterThan(Integer a, int b) {
        return a != null && a > b;
    }

    public static boolean greaterThan(Integer a, Integer b) {
        return a != null && b != null && a > b;
    }

    public static boolean greaterThan(Long a, long b) {
        return a != null && a > b;
    }

    public static boolean greaterThan(long a, Long b) {
        return b != null && a > b;
    }

    public static boolean greaterThan(Long a, Long b) {
        return a != null && b != null && a > b;
    }

    public static boolean greaterThan(BigDecimal a, Integer b) {
        BigDecimal bb = null;
        if (null != b) {
            bb = BigDecimal.valueOf(b);
        }
        return greaterThan(a, bb);
    }

    public static boolean greaterThan(BigDecimal a, Long b) {
        BigDecimal bb = null;
        if (null != b) {
            bb = BigDecimal.valueOf(b);
        }
        return greaterThan(a, bb);
    }
    // endregion

    public static boolean greaterThan(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return a.compareTo(b) > 0;
    }

    // region greaterThanZero
    public static boolean greaterThanZero(Integer a) {
        return greaterThan(a, 0);
    }

    public static boolean greaterThanZero(Long a) {
        return greaterThan(a, 0);
    }
    // endregion

    public static boolean greaterThanZero(BigDecimal a) {
        return greaterThan(a, 0);
    }

    // region greaterOrEqual
    public static boolean greaterThanOrEqual(int a, Integer b) {
        return b != null && a >= b;
    }

    public static boolean greaterThanOrEqual(Integer a, int b) {
        return a != null && a >= b;
    }

    public static boolean greaterThanOrEqual(Integer a, Integer b) {
        return a != null && b != null && a >= b;
    }

    public static boolean greaterThanOrEqual(Long a, long b) {
        return a != null && a >= b;
    }

    public static boolean greaterThanOrEqual(long a, Long b) {
        return b != null && a >= b;
    }

    public static boolean greaterThanOrEqual(Long a, Long b) {
        return a != null && b != null && a >= b;
    }

    public static boolean greaterThanOrEqual(BigDecimal a, Integer b) {
        BigDecimal bb = null;
        if (null != b) {
            bb = BigDecimal.valueOf(b);
        }
        return greaterThanOrEqual(a, bb);
    }

    public static boolean greaterThanOrEqual(BigDecimal a, Long b) {
        BigDecimal bb = null;
        if (null != b) {
            bb = BigDecimal.valueOf(b);
        }
        return greaterThanOrEqual(a, bb);
    }
    // endregion

    public static boolean greaterThanOrEqual(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return a.compareTo(b) >= 0;
    }

    // region lessThanOrEqual
    public static boolean lessThanOrEqual(int a, Integer b) {
        return b != null && a <= b;
    }

    public static boolean lessThanOrEqual(Integer a, int b) {
        return a != null && a <= b;
    }

    public static boolean lessThanOrEqual(Integer a, Integer b) {
        return a != null && b != null && a <= b;
    }

    public static boolean lessThanOrEqual(Long a, long b) {
        return a != null && a <= b;
    }

    public static boolean lessThanOrEqual(long a, Long b) {
        return b != null && a <= b;
    }

    public static boolean lessThanOrEqual(Long a, Long b) {
        return a != null && b != null && a <= b;
    }

    public static boolean lessThanOrEqual(BigDecimal a, Integer b) {
        BigDecimal bb = null;
        if (null != b) {
            bb = BigDecimal.valueOf(b);
        }
        return lessThanOrEqual(a, bb);
    }

    public static boolean lessThanOrEqual(BigDecimal a, Long b) {
        BigDecimal bb = null;
        if (null != b) {
            bb = BigDecimal.valueOf(b);
        }
        return lessThanOrEqual(a, bb);
    }
    // endregion

    public static boolean lessThanOrEqual(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return a.compareTo(b) <= 0;
    }

    // region lessThan
    public static boolean lessThan(Integer a, int b) {
        return a != null && a < b;
    }

    public static boolean lessThan(int a, Integer b) {
        return b != null && a < b;
    }

    public static boolean lessThan(Integer a, Integer b) {
        return a != null && b != null && a < b;
    }

    public static boolean lessThan(Long a, long b) {
        return a != null && a < b;
    }

    public static boolean lessThan(long a, Long b) {
        return b != null && a < b;
    }

    public static boolean lessThan(Long a, Long b) {
        return a != null && b != null && a < b;
    }

    public static boolean lessThan(BigDecimal a, Integer b) {
        BigDecimal bb = null;
        if (null != b) {
            bb = BigDecimal.valueOf(b);
        }
        return lessThan(a, bb);
    }

    public static boolean lessThan(BigDecimal a, Long b) {
        BigDecimal bb = null;
        if (null != b) {
            bb = BigDecimal.valueOf(b);
        }
        return lessThan(a, bb);
    }
    // endregion

    public static boolean lessThan(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return a.compareTo(b) < 0;
    }

    // region lessThanZero
    public static boolean lessThanZero(Integer a) {
        return lessThan(a, 0);
    }

    public static boolean lessThanZero(Long a) {
        return lessThan(a, 0);
    }
    // endregion

    public static boolean lessThanZero(BigDecimal a) {
        return lessThan(a, 0);
    }

    // region lessOrEqualZero
    public static boolean lessOrEqualZero(Integer a) {
        return a != null && a <= 0;
    }

    // endregion

    public static boolean lessOrEqualZero(BigDecimal a) {
        return a != null && a.compareTo(BigDecimal.ZERO) <= 0;
    }

    // region greaterOrEqualZero
    public static boolean greaterOrEqualZero(Integer a) {
        return a != null && a >= 0;
    }

    public static boolean greaterOrEqualZero(Long a) {
        return a != null && a >= 0;
    }
    // endregion

    public static boolean greaterOrEqualZero(BigDecimal a) {
        return a != null && a.compareTo(BigDecimal.ZERO) >= 0;
    }

    // region 废弃的方法
    @Deprecated
    public static boolean notNullAndEquals(Integer a, Integer b) {
        // 建议使用 notNullAndEqual(Integer a, Integer b 方法, 方法名少一个字母
        return notNullAndEqual(a, b);
    }
    // endregion

    @Deprecated
    public static boolean notNullAndEquals(Long a, Long b) {
        // 建议使用 notNullAndEqual(Long a, Long b) 方法, 方法名少一个字母
        return notNullAndEqual(a, b);
    }

    public static boolean equalZero(Integer a) {
        return a != null && a == 0;
    }

    public static boolean equalZero(Long a) {
        return a != null && a == 0;
    }

    public static boolean equalZero(BigDecimal a) {
        if (a == null) {
            return true;
        } else {
            return a.compareTo(BigDecimal.ZERO) == 0;
        }

    }

    public static boolean notNullAndZero(Integer a) {
        if (null == a) {
            return false;
        }
        return a != 0;
    }

    public static boolean notNullAndZero(Long a) {
        if (null == a) {
            return false;
        }
        return a != 0;
    }

    public static boolean notNullAndZero(BigDecimal a) {
        if (null == a) {
            return false;
        }
        return a.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 判断版本
     *
     * @param version1 版本号1
     * @param version2 版本号2
     * @return 大于 正数 小于 负数 等于 0
     */
    public static int versionCompare(String version1, String version2) {
        if (StringUtils.isEmpty(version1)) {
            return -1;
        }
        if (StringUtils.isEmpty(version2)) {
            return 1;
        }
        String[] versions1 = version1.split("\\.");
        String[] versions2 = version2.split("\\.");
        int length = version1.length();
        if (version1.length() != version2.length()) {
            length = Math.max(version1.length(), version2.length());
        }
        for (int i = 0; i < length; i++) {
            int v1, v2;
            try {
                v1 = Integer.parseInt(versions1[i]);
            } catch (IndexOutOfBoundsException e) {
                v1 = 0;
            }
            try {
                v2 = Integer.parseInt(versions2[i]);
            } catch (IndexOutOfBoundsException e) {
                v2 = 0;
            }
            int result = v1 - v2;
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public static boolean versionGreaterThanOrEqual(String v1, String v2) {
        return versionCompare(v1, v2) >= 0;
    }
}
