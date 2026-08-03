package cn.gudqs7.plugins.common.util;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Jeff.Liu
 * @version 1.0
 * @date 2021-04-01
 */
public class TypeCastUtils {

    private TypeCastUtils(){}

    /**
     *
     * @param value 参数
     * @return 返回值
     */
    public static String toString(Object value) {
        if (CommonUtils.isNotNull(value)) {
            if (value instanceof String) {
                return (String) value;
            } else if (value instanceof Integer) {
                return Integer.toString((Integer)value);
            } else if (value instanceof Long) {
                return value.toString();
            } else if (value instanceof Double) {
                return Double.toString((Double)value);
            } else {
                return value.toString();
            }
        }

        return "";
    }

    /**
     *
     * @param value 参数
     * @return 返回值
     */
    public static Integer toInteger(Object value) {
        if (CommonUtils.isNotNull(value)) {
            if (value instanceof String) {
                String varStr = (String)value;
                varStr = varStr.trim();
                return Integer.valueOf(varStr);
            } else if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Long) {
                return ((Long) value).intValue();
            } else if (value instanceof Float) {
                return ((Float) value).intValue();
            } else if (value instanceof Double) {
                return ((Double) value).intValue();
            } else if (value instanceof BigDecimal) {
                return ((BigDecimal) value).intValue();
            } else if (value instanceof Boolean) {
                return Boolean.TRUE.equals(value) ? 1 : 0;
            }
        }
        return 0;
    }


    /**
     *
     * @param value 参数
     * @return 返回值
     */
    public static Long toLong(Object value) {
        if (CommonUtils.isNotNull(value)) {
            if (value instanceof String) {
                String varStr = (String)value;
                varStr = varStr.trim();
                return Long.valueOf(varStr);
            } else if (value instanceof Integer) {
                return (Long) value;
            } else if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Float) {
                return ((Float) value).longValue();
            } else if (value instanceof Double) {
                return ((Double) value).longValue();
            } else if (value instanceof BigDecimal) {
                return ((BigDecimal) value).longValue();
            }else if (value instanceof Boolean) {
                return Boolean.TRUE.equals(value) ? 1L : 0L;
            }
        }
        return 0L;
    }


    /**
     *
     * @param value 参数
     * @return 返回值
     */
    public static BigDecimal toBigDecimal(Object value) {
        if (CommonUtils.isNotNull(value)) {
            if (value instanceof String) {
                String varStr = (String)value;
                varStr = varStr.trim();
                return new BigDecimal(varStr);
            } else if (value instanceof Integer) {
                return new BigDecimal((Integer)value);
            } else if (value instanceof Long) {
                return new BigDecimal((Long)value);
            } else if (value instanceof Float) {
                return BigDecimal.valueOf((Float) value);
            } else if (value instanceof Double) {
                return BigDecimal.valueOf((Double) value);
            } else if (value instanceof BigDecimal) {
                return ((BigDecimal) value);
            }else if (value instanceof Boolean) {
                return Boolean.TRUE.equals(value) ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     *
     * @param value 参数
     * @return 返回值
     */
    public static String toMoney(Object value) {
        BigDecimal money = TypeCastUtils.toBigDecimal(value);
        if (CommonUtils.isNotNull(value)) {
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.HALF_UP); // 设置模式为四舍五入
            return df.format(money);
        }
        return "0.00";
    }

    /**
     *
     * @param value 参数
     * @return 返回值
     */
    public static Boolean toBoolean(Object value) {
        if (value == null)  {
            return false;
        }
        if (value instanceof String) {
            return CommonUtils.isNotNull((String) value);
        } else if (value instanceof Integer) {
            return 0 != (Integer) value;
        } else if (value instanceof Long) {
            return 0L != (Long) value;
        } else if (value instanceof Float) {
            return 0.0 != (Float) value;
        } else if (value instanceof Double) {
            return 0.0!= (Double) value;
        } else if (value instanceof BigDecimal) {
            return BigDecimal.ZERO.equals(value);
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }

}
