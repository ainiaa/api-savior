package cn.gudqs7.plugins.common.util;

import cn.gudqs7.plugins.common.consts.CommonConstants;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.startsWith;

/**
 * @date: Created in 2020/09/24.
 * @author: Town
 * @slogan: 叮咚买菜
 * @description: 检查工具类
 */
public class CommonUtils {


    /**
     * 手机号正则
     */
    protected static final Pattern MOBILE_PATTERN = Pattern.compile("^1\\d{2}.{4}\\d{4}$");

    /**
     * 金额正则
     */
    protected static final Pattern AMOUNT_PATTERN = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");

    /**
     * 空白字符   \s 空格
     */
    protected static final Pattern BLANK_PATTERN = Pattern.compile("\\s*|\t|\n|\r|\\v");

    /**
     * 判断一个对象是不是Null
     */
    public static <T> boolean isNull(T obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return CommonConstants.BLANK.equals(obj);
        }
        if (obj instanceof Collection) {
            return ((Collection<?>)obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>)obj).isEmpty();
        }
        return false;
    }

    /**
     * 判断一个对象是不是不是Null
     */
    public static <T> boolean isNotNull(T obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            return !CommonConstants.BLANK.equals(obj);
        }
        if (obj instanceof Collection) {
            return !((Collection<?>)obj).isEmpty();
        }
        if (obj instanceof Map) {
            return !((Map<?, ?>)obj).isEmpty();
        }
        return true;
    }

    /**
     *
     * @param obj
     * @return
     */
    public static boolean isFalseOrNull(Boolean obj) {
        if (obj == null) {
            return true;
        }
        return Boolean.FALSE.equals(obj);
    }

    /**
     *
     * @param obj
     * @return
     */
    public static boolean isTrue(Boolean obj) {
        if (obj == null) {
            return false;
        }
        return Boolean.TRUE.equals(obj);
    }

    /**
     * 检测传递的值是否为空或者小于0,如果为空或者小于0 则返回false
     * [废弃的方法-请使用com.ddmc.trade.backward.app.common.util.CompareUtil工具类的greaterThanZero方法]
     *
     * @param value Integer类型数据
     * @return true 大于0 false 小于0
     */
    @Deprecated
    public static boolean verifyBigThanZero(Integer value) {

        return Optional.ofNullable(value)
                .map(s -> s > 0)
                .orElse(false);
    }

    /**
     * 检测传递的值是否为空或者小于0,如果为空或者小于0 则返回false
     * [废弃的方法-请使用com.ddmc.trade.backward.app.common.util.CompareUtil工具类的greaterThanZero方法]
     *
     * @param value Long类型数据
     * @return true 大于0 false 小于0
     */
    @Deprecated
    public static boolean verifyBigThanZero(Long value) {

        return Optional.ofNullable(value)
                .map(s -> s > 0)
                .orElse(false);
    }

    /**
     * 检测传递的值是否为空或者小于0,如果为空或者小于0 则返回false
     * [废弃的方法-请使用com.ddmc.trade.backward.app.common.util.CompareUtil工具类的greaterThanZero方法]
     *
     * @param value BigDecimal类型数据
     * @return true 大于0 false 小于0
     */
    @Deprecated
    public static boolean verifyBigThanZero(BigDecimal value) {

        return Optional.ofNullable(value)
                .map(s -> s.doubleValue() > 0.0)
                .orElse(false);
    }

    /**
     * 金额保留两位小数
     *
     * @param money
     * @return
     */
    public static String saveMoneyBy2(String money) {
        if (isNull(money)) {
            return new BigDecimal("0.00").toString();
        }
        BigDecimal bigDecimal = new BigDecimal(money)
                .setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.toString();
    }

    /**
     * 金额保留两位小数
     *
     * @param money
     * @return
     */
    public static Integer saveMoneyBy2Integer(BigDecimal money) {

        return getMoneyBy2(money).intValue();
    }

    /**
     * 金额保留两位小数
     *
     * @param money
     * @return
     */
    public static String saveMoneyBy2(BigDecimal money) {

        return getMoneyBy2(money).toString();
    }

    public static BigDecimal getMoneyBy2(BigDecimal money) {
        if (isNull(money)) {
            return new BigDecimal("0.00");
        }
        return money.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getMoneyBy2DefaultNull(BigDecimal money) {
        if (isNull(money)) {
            return null;
        }
        BigDecimal bigDecimal = money.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal;
    }

    public static BigDecimal getMoneyBy2DefaultZero(BigDecimal money) {
        if (isNull(money)) {
            return BigDecimal.ZERO;
        }
        BigDecimal bigDecimal = money.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal;
    }

    public static BigDecimal getMoneyBy2(String money) {
        if (isNull(money)) {
            return null;
        }
        Matcher matcher = AMOUNT_PATTERN.matcher(money);
        boolean isMatcher = matcher.matches();
        if (!isMatcher) {
            return new BigDecimal("0.00");
        }
        BigDecimal bigDecimal = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal;
    }

    public static BigDecimal getMoneyBy2DefaultNull(String money) {
        if (isNull(money)) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal;
    }

    public static BigDecimal getMoneyBy2DefaultNull(Integer money) {
        if (isNull(money)) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(money);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal;
    }


    /**
     * 对bigdecimal类型的数据做非空处理，如果不为空则返回,为空则赋值为0
     *
     * @param value bigdecimal类型的数据
     * @return 非空bigdecimal类型的数据
     */
    public static BigDecimal getAndSetValue(BigDecimal value) {

        return Optional.ofNullable(value)
                .filter(CommonUtils::isNotNull)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 对integer类型的数据做非空处理，如果不为空则返回,为空则赋值为0
     *
     * @param value integer类型的数据
     * @return 非空integer类型的数据
     */
    public static String getAndSetValueString(Integer value) {

        if (isNull(value)) {
            return CommonConstants.BLANK;
        }
        String str = String.valueOf(value);
        return Optional.of(str)
                .filter(CommonUtils::isNotNull)
                .orElse(CommonConstants.BLANK);
    }

    /**
     * 对String类型的数据做非空处理，如果不为空则返回,为空则赋值为0
     *
     * @param value integer类型的数据
     * @return 非空integer类型的数据
     */
    public static Integer getAndSetValueInteger(String value) {

        if (isNull(value)) {
            return 0;
        }
        Integer num = Integer.parseInt(value);
        return Optional.of(num)
                .filter(CommonUtils::isNotNull)
                .orElse(0);
    }

    /**
     * 对String类型的数据做非空处理，如果不为空则返回,为空则赋值为0
     *
     * @param value integer类型的数据
     * @return 非空integer类型的数据
     */
    public static Long getAndSetValueLong(String value) {

        if (isNull(value)) {
            return 0L;
        }
        Long num = Long.parseLong(value);
        return Optional.of(num)
                .filter(CommonUtils::isNotNull)
                .orElse(0L);
    }

    /**
     * 对integer类型的数据做非空处理，如果不为空则返回,为空则赋值为0
     *
     * @param value integer类型的数据
     * @return 非空integer类型的数据
     */
    public static BigDecimal getAndSetValueBigDecimal(Long value) {

        if (isNull(value)) {
            return BigDecimal.ZERO;
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return Optional.of(bigDecimal)
                .filter(CommonUtils::isNotNull)
                .orElse(BigDecimal.ZERO);
    }


    /**
     * 对bigdecimal类型的数据做非空处理，如果不为空则返回,为空则赋值为0
     *
     * @param value bigdecimal类型的数据
     * @return 非空bigdecimal类型的数据
     */
    public static BigDecimal getAndSetValueBigDecimal(String value) {
        if (isNull(value)) {
            return BigDecimal.ZERO;
        }
        BigDecimal bigDecimal = new BigDecimal(value);
        return Optional.of(bigDecimal)
                .filter(CommonUtils::isNotNull)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 对string做非空处理，若不为空则直接返回，若为空则返回空字符串
     *
     * @param value string类型数据
     * @return 非空string类型数据
     */
    public static String getAndSetValue(String value) {

        return Optional.ofNullable(value)
                .filter(CommonUtils::isNotNull)
                .orElse(StringUtils.EMPTY);
    }

    /**
     * 对integer类型的数据做非空处理，如果不为空则返回,为空则赋值为0
     *
     * @param value integer类型的数据
     * @return 非空integer类型的数据
     */
    public static Integer getAndSetValue(Integer value) {

        return Optional.ofNullable(value)
                .filter(CommonUtils::isNotNull)
                .orElse(0);
    }

    /**
     * 对Long做非空处理，若不为空则直接返回，若为空则返回0
     *
     * @param value Long类型数据
     * @return 非空Long类型数据
     */
    public static Long getAndSetValue(Long value) {

        return Optional.ofNullable(value)
                .filter(CommonUtils::isNotNull)
                .orElse(0L);
    }


    /**
     * 对bigdecimal类型的数据做非空处理，如果不为空则返回,为空则赋值为0
     *
     * @param value bigdecimal类型的数据
     * @return 非空bigdecimal类型的数据
     */
    public static BigDecimal getAndSetValueBigDecimal(Integer value) {
        if (isNull(value)) {
            return BigDecimal.ZERO;
        }
        BigDecimal bigDecimal = new BigDecimal(value);
        return Optional.of(bigDecimal)
                .filter(CommonUtils::isNotNull)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 集合为null时 设置空集合
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> List getAndSetList(List<T> list) {
        if (list == null) {
            return new ArrayList();
        }
        return list;
    }

    /**
     * desc 简单校验 (以1开头的11位数) 验证手机号
     *
     * @param mobiles 手机号码
     * @return 有效返回true, 否则返回false
     */
    public static boolean isMobileNo(String mobiles) {
        boolean isMobileNo = false;
        if (StringUtils.isNotBlank(mobiles)) {
            Matcher m = MOBILE_PATTERN.matcher(mobiles);
            isMobileNo = m.matches();
        }
        return isMobileNo;
    }

    /**
     * 隐私手机号
     *
     * @param phone
     * @return
     */
    public static String hiddenMobile(String phone) {
        boolean mobileNo = isMobileNo(phone);
        if (!mobileNo) {
            return null;
        }
        String first3ByPhone = phone.substring(0, 3);
        String last4ByPhone = phone.substring(7);
        return new StringBuffer(first3ByPhone).append("****").append(last4ByPhone).toString();
    }

    /**
     * 兼容旧代码，将URL以http开头替换为https
     *
     * @param url 目标URL
     * @return 更换后的URL
     */
    public static String httpToHttps(String url) {
        if (startsWith(url, "https")) {
            return url;
        }
        return replace(url, "http", "https", 1);
    }

    /**
     * 在非空数值a、b中获取最小值
     */
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    /**
     * 在非空数值a、b中获取最小值
     */
    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    /**
     * 在非空数值a、b中获取最大值
     */
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    /**
     * 检测传递的值是否为空或者等于0,如果为空或者等于0 则返回false
     *
     * @param value Integer类型数据
     * @return true 不等于0 false 等于0
     */
    public static boolean isNotNullAndZero(Integer value) {

        return Optional.ofNullable(value)
                .map(s -> s != 0)
                .orElse(false);
    }

    /**
     * 检测传递的值是否为空或者等于0,如果为空或者等于0 则返回false
     *
     * @param value Integer类型数据
     * @return true 不等于0 false 等于0
     */
    public static boolean isNotNullAndZero(Long value) {

        return Optional.ofNullable(value)
                .map(s -> s != 0)
                .orElse(false);
    }

    /**
     * 构造key
     * @param id
     * @param number
     * @return
     */
    public static String buildKey(String id, Integer number) {
        StringBuilder key = new StringBuilder(id);
        if (null != number) {
            key.append("_").append(number);
        }
        return key.toString();
    }


    /**
     * 过滤特殊字符
     *
     * @param origin
     * @return
     */
    public static  String stripSpecialCharacter(String origin) {
        origin = origin.replace((char)194, ' ');
        origin = origin.replace((char)160, ' ');
        origin = origin.replace('\'', '*');
        origin = origin.replace('º', '*');
        origin = origin.replace('•', '*');
        origin = origin.replace('&', '*');
        return origin;
    }

    /**
     * 过滤空白字符
     * @param origin
     * @return
     */
    public static String stripBlankCharacter(String origin) {
        if (CommonUtils.isNotNull(origin)) {
            Matcher m = BLANK_PATTERN.matcher(origin);
            return m.replaceAll("");
        }
        return "";
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名  eg .java
     */
    public static String getFileExtension(String fileName) {
        if (CommonUtils.isNull(fileName)) {
            return "";
        }
        return  fileName.substring(fileName.lastIndexOf("."));
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
