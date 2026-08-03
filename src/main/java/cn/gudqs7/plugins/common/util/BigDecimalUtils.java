package cn.gudqs7.plugins.common.util;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Jeff.Liu
 * @date 2021-08-02
 */
public class BigDecimalUtils {

    public static final BigDecimal MONEY_TOLERANCE = new BigDecimal("0.001");

    public static BigDecimal getValue(BigDecimal a) {
        return Optional.ofNullable(a).orElse(BigDecimal.ZERO);
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return getValue(a).add(getValue(b));
    }

    public static BigDecimal add(BigDecimal... numbers) {
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal number : numbers) {
            result = result.add(getValue(number));
        }
        return result;
    }

    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return getValue(a).subtract(getValue(b));
    }

    public static BigDecimal subtract(BigDecimal... numbers) {
        BigDecimal result = null;
        for (BigDecimal number : numbers) {
            if (null == result) {
                result = getValue(number);
            } else {
                result = result.subtract(getValue(number));
            }
        }
        return result;
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return getValue(a).multiply(getValue(b));
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b,int scale, RoundingMode roundingMode) {
        return getValue(a).multiply(getValue(b)).setScale(scale, roundingMode);
    }

    public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale, RoundingMode roundingMode) {
        return getValue(a).divide(getValue(b), scale, roundingMode);
    }

    /**
     * 拆分 BigDecimal
     * @param a
     * @param max
     * @return
     */
    public static List<BigDecimal> toList(BigDecimal a, BigDecimal max) {
        List<BigDecimal> list = new ArrayList<>();
        while (CompareUtils.greaterThanOrEqual(a, max) && CompareUtils.greaterThanZero(max)) {
            list.add(max);
            a = a.subtract(max);
        }
        if (CompareUtils.greaterThanZero(a)) {
            list.add(a);
        }
        return list;
    }

    private BigDecimalUtils(){}

    /**
     * 获取参数 默认 0
     * @param value
     * @return
     */
    public static BigDecimal getDefaultValue(BigDecimal value) {
        return getDefaultValue(value, BigDecimal.ZERO);
    }

    /**
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static BigDecimal getDefaultValue(BigDecimal value, BigDecimal defaultValue) {
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    public static boolean areEqual(BigDecimal a, BigDecimal b, BigDecimal tolerance) {
        BigDecimal diff = subtract(a, b);
        return CompareUtils.lessThanOrEqual(diff.abs(), tolerance);
    }

    public static boolean approachToZero(BigDecimal a) {
        return CompareUtils.lessThanOrEqual(a, MONEY_TOLERANCE);
    }

    /**
     *
     * @param money
     * @return
     */
    public static String toMoneyYuan(BigDecimal money) {
        BigDecimal taxPrice = money.setScale(2, RoundingMode.HALF_UP);
        // 保留2位小数，不足补0
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return decimalFormat.format(taxPrice);
    }

    /**
     *
     * @param money
     * @return
     */
    public static BigDecimal toMoneyYuanBigDecimal(BigDecimal money) {
        return money.setScale(2, RoundingMode.HALF_UP);
    }
}
