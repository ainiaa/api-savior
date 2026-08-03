package cn.gudqs7.plugins.common.consts;

import java.math.BigDecimal;

/**
 * 业务常量静态类
 * @author Jeff.Liu
 * @date 2021-06-19
 */
public final class CommonConstants {

    public static final String DATE_NO_SEPARATOR = "yyyyMMdd";

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 通用标记  是
     */
    public static final int GENERAL_YES = 1;

    /**
     * 通用标记  否
     */
    public static final int GENERAL_NO = 0;


    public static final int BATCH_INSERT_QTY = 100;

    public static final String UNIT_NAME = "份";

    /**
     * 空字符串
     */
    public static final String ZERO = "0";

    /**
     * 空字符串
     */
    public static final String BLANK = "";

    public static final String SUCCESS = "success";

    public static final Integer INTEGER_SUCCESS = 0;
    public static final String SUCCESS_CODE = "0";

    public static final Integer INTEGER_FAILURE = -1;
    public static final String FAILURE = "fail";

    public static final int DEFAULT_PAGE = 100;

    public static final double MIN_MONEY = 0.001;

    public static final BigDecimal MONEY_TOLERANCE = new BigDecimal("0.001");
    private CommonConstants() {
    }
}
