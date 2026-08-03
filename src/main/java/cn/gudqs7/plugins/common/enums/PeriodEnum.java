package cn.gudqs7.plugins.common.enums;

import lombok.Getter;

/**
 * @author Jeff.Liu
 * @date 2021-12-14
 */
@Getter
public enum PeriodEnum implements StringEnumInterface {

    /**
     * 秒
     */
    PERIOD_SECOND("second", "秒"),

    /**
     * 分钟
     */
    PERIOD_MINUTE("minute", "分钟"),

    /**
     * 小时
     */
    PERIOD_HOUR("hour", "小时"),

    /**
     * 天
     */
    PERIOD_DAY("day", "天"),

    /**
     * 周
     */
    PERIOD_WEEK("week", "周"),

    /**
     * 月
     */
    PERIOD_MONTH("month", "月"),

    /**
     * 年
     */
    PERIOD_YEAR("year", "年"),
    ;

    /**
     * 描述
     */
    private final String desc;

    /**
     * code
     */
    private final String code;

    PeriodEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }
}