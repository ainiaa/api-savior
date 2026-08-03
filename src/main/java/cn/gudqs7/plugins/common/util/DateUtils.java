package cn.gudqs7.plugins.common.util;



import cn.gudqs7.plugins.common.enums.PeriodEnum;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Jeff.Liu
 * @date 2020/8/19
 */
public class DateUtils {

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private static final ThreadLocal<DateFormat> yyyyMMdd = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    private static final ThreadLocal<DateFormat> yyyyMM = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMM"));

    private static final ThreadLocal<DateFormat> yyyy = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy"));

    private static final ThreadLocal<DateFormat> yyyyMMddHHmmAcross = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm"));

    private static final ThreadLocal<DateFormat> yyyyMMddHHmmssAcross = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    private static final ThreadLocal<DateFormat> yyyyMMddHHmmss = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));
    private static final ThreadLocal<DateFormat> yyyyMMddHHmmSSS = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmssSSS"));

    private static final ThreadLocal<DateFormat> yyyyMMddAcross = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    private static final ThreadLocal<DateFormat> yyyyMMddLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy年MM月dd日"));

    private static final ThreadLocal<DateTimeFormatter> dtyyyyMMddHHmmss = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    private static final ThreadLocal<DateTimeFormatter> dtyyyyMMdd = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyyMMdd"));

    private static final ThreadLocal<DateTimeFormatter> dtyyyyMM = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyyMM"));
    private static final ThreadLocal<DateTimeFormatter> dtyyyyMMAcross = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy-MM"));

    private static final ThreadLocal<DateTimeFormatter> dtyyyy = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy"));

    private static final ThreadLocal<DateTimeFormatter> dtyyyyMMddHHmmAcross = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

    private static final ThreadLocal<DateTimeFormatter> dtyyyyMMddHHmmssAcross = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    private static final ThreadLocal<DateTimeFormatter> dtyyyyMMddAcross = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    private static final ThreadLocal<DateTimeFormatter> dtyyyyMMddLocal = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy年MM月dd日"));

    public static String formatYyyyMMdd(Date date) {
        if (null == date) {
            return null;
        }
        return yyyyMMdd.get().format(date);
    }

    public static String formatYyyyMMddLocal(Date date) {
        if (null == date) {
            return null;
        }
        return yyyyMMddLocal.get().format(date);
    }

    public static Date parseYyyyMMdd(String date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyyMMdd.get().parse(date);
        } catch (ParseException e) {

        }
        return null;
    }

    public static String formatYyyyMM(Date date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyyMM.get().format(date);
        }catch (Exception e){
            return null;
        }
    }

    public static String formatYyyyMM(LocalDateTime date) {
        if (null == date) {
            return null;
        }
        try {
            return dtyyyyMM.get().format(date);
        }catch (Exception e){
            return null;
        }
    }

    public static String formatYyyyMMddAcross(Date date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyyMMddAcross.get().format(date);
        }catch (Exception e){
            return null;
        }
    }

    public static String formatYyyyMMddAcross(LocalDateTime date) {
        if (null == date) {
            return null;
        }
        try {
            return dtyyyyMMddAcross.get().format(date);
        }catch (Exception e){
            return null;
        }
    }

    public static Date parseYyyyMMddAcross(String date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyyMMddAcross.get().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("时间解析异常:" + date, e);
        }
    }

    public static String formatYyyyMMddHHmmAcross(Long millis) {
        if (null == millis) {
            return null;
        }
        if (isSecondTimestamp(millis)) {
            millis = getMillisecondsTimestamp(millis);
        }
        return yyyyMMddHHmmAcross.get().format(millis);
    }

    public static String formatYyyyMMddHHmmAcrossBySeconds(Long seconds) {
        if (null == seconds) {
            return null;
        }
        return formatYyyyMMddHHmmAcross(getMillisecondsTimestamp(seconds));
    }

    public static String formatYyyyMMddHHmmssAcrossBySeconds(Long seconds) {
        if (null == seconds) {
            return null;
        }
        return formatYyyyMMddHHmmssAcross(getMillisecondsTimestamp(seconds));
    }

    public static String formatYyyyMMddAcrossBySeconds(Long seconds) {
        if (null == seconds) {
            return null;
        }

        return formatYyyyMMddAcross(getMillisecondsTimestamp(seconds));
    }

    public static String formatYyyyMMddBySeconds(Long seconds) {
        if (null == seconds) {
            return null;
        }
        return formatYyyyMMddMillis(getMillisecondsTimestamp(seconds));
    }

    public static Date parseYyyyMMddHHmmAcross(String date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyyMMddHHmmAcross.get().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("时间解析异常:" + date, e);
        }
    }

    public static String formatYyyyMMddHHmmssAcross(Date date) {
        if (null == date) {
            return null;
        }
        return yyyyMMddHHmmssAcross.get().format(date);
    }

    public static String formatYyyyMMddHHmmssAcross(LocalDateTime dateTime) {
        if (null == dateTime) {
            return null;
        }
        return dtyyyyMMddHHmmssAcross.get().format(dateTime);
    }


    public static String formatYyyyMMddHHmmssAcross(Long millis) {
        if (null == millis) {
            return null;
        }
        return yyyyMMddHHmmssAcross.get().format(millis);
    }

    public static String formatYyyyMMddAcross(Long millis) {
        if (null == millis) {
            return null;
        }
        return yyyyMMddAcross.get().format(millis);
    }

    public static String formatYyyyMMddMillis(Long millis) {
        if (null == millis) {
            return null;
        }
        if (isSecondTimestamp(millis)) {
            millis = getMillisecondsTimestamp(millis);
        }
        return yyyyMMdd.get().format(millis);
    }

    public static Date parseYyyyMMddHHmmssAcross(String date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyyMMddHHmmssAcross.get().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("时间解析异常:" + date, e);
        }
    }

    public static Date parseYyyyMMddHHmmss(String date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyyMMddHHmmss.get().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("时间解析异常:" + date, e);
        }
    }

    public static Date parseYyyyMM(String date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyyMM.get().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("时间解析异常:" + date, e);
        }
    }

    public static String formatYyyy(Date date) {
        if (null == date) {
            return null;
        }
        return yyyy.get().format(date);
    }

    public static Date parse_yyyy(String date) {
        if (null == date) {
            return null;
        }
        try {
            return yyyy.get().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("时间解析异常:" + date, e);
        }
    }

    public static Date truncatedToDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Long getCurrentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static String formatDate(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static String formatDate(Long seconds, String pattern) {
        if (null == seconds) {
            return null;
        }
        return formatDate(new Date(seconds * 1000L), pattern);
    }


    @Deprecated
    public static Long parseToTimestamp(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        if (CommonUtils.isNotNull(localDate)) {
            return localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli() / 1000;
        }
        return 0L;
    }


    @Deprecated
    public static Long parseToTimestamp(Date date) {
        if (null == date) {
            return null;
        }
        if (CommonUtils.isNotNull(date)) {
            return date.getTime() / 1000;
        }
        return 0L;
    }

    @Deprecated
    public static Long parseToTimestamp(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        if (CommonUtils.isNotNull(localDateTime)) {
            return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
        }
        return 0L;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past 第几天
     * @return 日期
     */
    public static String getPastDateString(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        String result = yyyyMMddAcross.get().format(today);
        return result;
    }


    /**
     * 获取过去第几天的日期
     * 格式 yyyy-MM-dd HH:mm:ss
     * @param past 第几天
     * @return 日期
     */
    public static String getPastDateDetailString(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        String result = yyyyMMddHHmmssAcross.get().format(today);
        return result;
    }

    /**
     * 获取过去第几天的时间戳
     *
     * @param past 第几天
     * @return 时间戳 s
     */
    public static Long getPastDateSeconds(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long timeInSeconds = calendar.getTimeInMillis() / 1000;
        return timeInSeconds;
    }

    /**
     * 获取将来第几天的时间戳
     *
     * @param after 第几天
     * @return 时间戳 s
     */
    public static Long getAfterDateSeconds(int after) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + after);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long timeInSeconds = calendar.getTimeInMillis() / 1000;
        return timeInSeconds;
    }

    /**
     * 计算当前日期与凌晨秒差 单位s
     *
     * @return 秒差
     */
    public static Long getSecondsToBeforeDawn() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 计算当前日期 0 点时间戳
     *
     * @return 秒差
     */
    public static Long getSecondsStartOfToday() {
        long current = System.currentTimeMillis();
        //今天零点零分零秒的毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        return zero / 1000;
    }

    /**
     * 计算当前日期 0 点时间戳
     *
     * @param days
     * @return 秒差
     */
    public static Long getSecondsStartOfToday(int days) {
        long current = System.currentTimeMillis();
        //今天零点零分零秒的毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        return zero / 1000 + days * 86400;
    }

    /**
     * 计算当前日期 0 点时间戳
     *
     * @return 秒差
     */
    public static Long getSecondsEndOfToday() {
        long current = System.currentTimeMillis();
        //今天零点零分零秒的毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        long twelve = zero + 24 * 60 * 60 * 1000 - 1;
        return twelve / 1000;
    }

    /**
     * 计算当前日期 0 点时间戳
     *
     * @param days
     * @return 秒差
     */
    public static Long getSecondsEndOfToday(int days) {
        long current = System.currentTimeMillis();
        //今天零点零分零秒的毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        long twelve = zero + 24 * 60 * 60 * 1000 - 1;
        return twelve / 1000 + days * 86400;
    }

    /**
     * 对传入的日期，进行天数的增减
     *
     * @param date
     * @param day
     * @return
     */
    public static Date opDateOfDays(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, day);
        return cal.getTime();
    }

    /**
     * 获取一周的第几天
     * 1=Sunday,2=Monday,,,7=Saturday。
     *
     * @return
     */
    public static Integer getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * @param date
     * @return
     */
    public static Integer getDayOfWeek(Date date) {
        if (null == date) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd HH:mm:ss
     */
    public static Long parseyyyyMMddHHmmssAcrossToTimestamp(String time) {
        if (null == time) {
            return null;
        }
        try {
            DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime parse = LocalDateTime.parse(time, ftf);
            return LocalDateTime.from(parse).atZone(ZoneOffset.ofHours(8)).toInstant().toEpochMilli() / 1000;
        } catch (Exception e) {
            throw new RuntimeException("时间解析异常:" + time, e);
        }
    }

    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd
     */
    public static Long parseyyyyMMddAcrossToTimestampMilliSecond(String time) {
        if (null == time) {
            return null;
        }
        Date date = parseYyyyMMddAcross(time);
        return date.getTime();
    }

    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd
     */
    public static Long parseyyyyMMddAcrossToTimestampSecond(String time) {
        if (null == time) {
            return null;
        }
        Long millis = parseyyyyMMddAcrossToTimestampMilliSecond(time);
        return millis / 1000;
    }

    /**
     * 是否同月
     *
     * @param firstTime
     * @param secondTime
     * @return
     */
    public static Boolean isSameMonth(LocalDateTime firstTime, LocalDateTime secondTime) {
        return firstTime.getYear() == secondTime.getYear() && firstTime.getMonthValue() == secondTime.getMonthValue();
    }

    public static LocalDateTime parseDtYyyyMMdd(String date) {
        if (Objects.isNull(date)){
            return null;
        }
        LocalDate localDate = LocalDate.parse(date, dtyyyyMMdd.get());
        return LocalDateTime.of(localDate, LocalTime.MIN);
    }

    public static LocalDateTime parseDtyyyyMMddHHmmss(String date) {
        if (Objects.isNull(date)){
            return null;
        }
        LocalDate localDate = LocalDate.parse(date, dtyyyyMMddHHmmss.get());
        return LocalDateTime.of(localDate, LocalTime.MIN);
    }

    public static LocalDateTime parseDtyyyyMMddHHmmssDefaultNull(String date) {
        if (Objects.isNull(date)){
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(date, dtyyyyMMddHHmmss.get());
            return LocalDateTime.of(localDate, LocalTime.MIN);
        }catch (Exception e){
            return null;
        }
    }

    public static String formatYyyyMMddHHmmSSSMillis(Long millis) {
        if (null == millis) {
            return null;
        }
        return yyyyMMddHHmmSSS.get().format(millis);
    }


    /**
     * 获取将来第几分钟的时间字符串
     *
     * @param before 第几分钟
     * @return 时间戳 s
     */
    public static String getBeforeMinutesYyyyMMddHHmmssAcross(int before) {
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_YEAR, 0);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + before);
//        calendar.set(Calendar.MILLISECOND, 0);

        return formatYyyyMMddHHmmssAcross(calendar.getTime());
    }

    /**
     * 获取将来之前第几PERIOD的时间字符串
     *
     * @param after 第几 PERIOD
     * @return 时间戳 s
     */
    public static String getBeforePeriodYyyyMMddHHmmssAcross(int after, PeriodEnum periodEnum) {
        Calendar calendar = Calendar.getInstance();
        if (PeriodEnum.PERIOD_DAY == periodEnum) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + after);
        }

        if (PeriodEnum.PERIOD_HOUR == periodEnum) {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + after);
        }

        if (PeriodEnum.PERIOD_MINUTE == periodEnum) {
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + after);
        }
        if (PeriodEnum.PERIOD_SECOND == periodEnum) {
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + after);
        }

//        calendar.set(Calendar.MILLISECOND, 0);

        return formatYyyyMMddHHmmssAcross(calendar.getTime());
    }

    /**
     * todo 需要测试
     *
     * @param date
     * @return
     */
    public static LocalDateTime parseDtYyyyMM(String date) {
        if (null == date) {
            return null;
        }
        try {
            return LocalDateTime.parse(date, dtyyyyMM.get());
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * todo 需要测试
     *
     * @param date
     * @return
     */
    public static LocalDateTime parseDtYyyy(String date) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.parse(date, dtyyyy.get());
    }

    /**
     * todo 需要测试
     *
     * @param date
     * @return
     */
    public static LocalDateTime parseDtYyyyMMddHHmmAcross(String date) {
        if (null == date) {
            return null;
        }
        try {
            return LocalDateTime.parse(date, dtyyyyMMddHHmmAcross.get());
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * todo 需要测试
     *
     * @param date
     * @return
     */
    public static LocalDateTime parseDtYyyyMMddHHmmssAcross(String date) {
        if (null == date) {
            return null;
        }
        LocalDateTime ldt = null;
        try {
            ldt = LocalDateTime.parse(date, dtyyyyMMddHHmmssAcross.get());
        } catch (Exception e) {
        }
        return ldt;
    }

    /**
     *
     * @param date
     * @return
     */
    public static LocalDateTime parseDtYyyyMMddAcross(String date) {
        if (null == date) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(date, dtyyyyMMddAcross.get());
            return localDate.atStartOfDay();
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * todo 需要测试
     *
     * @param date
     * @return
     */
    public static LocalDateTime parseDtYyyyMMddLocal(String date) {
        if (null == date) {
            return null;
        }
        try {
            return LocalDateTime.parse(date, dtyyyyMMddLocal.get());
        } catch (Exception e) {
            return null;
        }

    }


    public static Long toTimestamp(LocalDateTime ldt) {
        long timestamp = 0L;
        if (ldt == null) {
            return timestamp;
        }

        try {
            timestamp = ldt.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
        } catch (Exception e) {
        }
        return timestamp;
    }


    public static Long toTimestamp(Date date) {
        if (CommonUtils.isNotNull(date)) {
            return date.getTime() / 1000;
        }
        return 0L;
    }

    public static String formatDtYyyyMMddAcross(LocalDateTime date) {
        try {
            return dtyyyyMMddAcross.get().format(date);
        } catch (Exception e) {
            return "";
        }

    }

    public static String formatDtYyyyMMdd(LocalDateTime date) {
        try {
            return dtyyyyMMdd.get().format(date);
        }catch (Exception e) {
            return "";
        }

    }

    public static String formatDtYyyyMMddHHmmssAcross(LocalDateTime date) {
        try {
            return dtyyyyMMddHHmmssAcross.get().format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 是否为秒级时间戳
     *
     * @param timestamp 时间戳
     * @return
     */
    public static boolean isSecondTimestamp(Long timestamp) {
        if (CommonUtils.isNull(timestamp)) {
            return false;
        }
        String timestampStr = String.valueOf(timestamp);
        return timestampStr.length() == 10;
    }

    /**
     * 是否为毫秒级时间戳
     *
     * @param timestamp 时间戳
     * @return
     */
    public static boolean isMillisecondsTimestamp(Long timestamp) {
        if (isZeroTimestamp(timestamp)) {
            return false;
        }
        String timestampStr = String.valueOf(timestamp);
        return timestampStr.length() == 13;
    }

    public static boolean isZeroTimestamp(Long timestamp) {
        if (CommonUtils.isNull(timestamp) || 0L == timestamp) {
            return true;
        }
        return false;
    }

    /**
     * 是否为秒级时间戳或者毫秒级时间戳
     *
     * @param timestamp 时间戳
     * @return
     */
    public static boolean isMillisecondsOrSecondTimestamp(Long timestamp) {
        return isSecondTimestamp(timestamp) || isMillisecondsTimestamp(timestamp);
    }

    /**
     * 获取毫秒级别时间戳
     *
     * @param timestamp 时间戳 (可能为秒、毫秒时间戳 也可能是其他)
     * @return
     */
    public static Long getMillisecondsTimestamp(Long timestamp) {
        if (isSecondTimestamp(timestamp)) {
            return timestamp * 1000;
        }
        return timestamp;
    }

    /**
     * 计算相差月数
     */
    public static long getMonthBetween(Date date1, Date date2) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate1 = date1.toInstant().atZone(zoneId).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(zoneId).toLocalDate();

        return ChronoUnit.MONTHS.between(localDate1, localDate2);
    }

    /**
     * 计算相差月数
     */
    public static long getMonthBetween(LocalDateTime date1, LocalDateTime date2) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate1 = date1.atZone(zoneId).toLocalDate();
        LocalDate localDate2 = date2.atZone(zoneId).toLocalDate();

        return ChronoUnit.MONTHS.between(localDate1, localDate2);
    }


    /**
     * 计算当前日期与当前月末秒差 单位s
     *
     * @return 秒差
     */
    public static Long getSecondsToBeforeMonth() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        ca.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        ca.set(Calendar.MINUTE, 59);
        //将秒至59
        ca.set(Calendar.SECOND, 59);
        //将毫秒至999
        ca.set(Calendar.MILLISECOND, 999);
        return (ca.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 计算相差天数
     * @param timeStamp1
     * @param timeStamp2
     * @return
     */
    public static long getDayBetween(long timeStamp1, long timeStamp2) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate1 = Instant.ofEpochMilli(timeStamp1).atZone(zoneId).toLocalDate();
        LocalDate localDate2 = Instant.ofEpochMilli(timeStamp2).atZone(zoneId).toLocalDate();
        return ChronoUnit.DAYS.between(localDate1, localDate2);
    }

    /**
     * 时间戳转LocalDateTime
     *
     * @param timestamp timestamp
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        if (isZeroTimestamp(timestamp)) {
            return null;
        }
        if (isSecondTimestamp(timestamp)) {
            timestamp = timestamp * 1000;
        }
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.of("+8"));
    }

    /**
     * date转LocalDateTime
     *
     * @param date date数据
     * @return 返回结果
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.of("+8"));
    }

    /**
     * 时间戳转LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDate toLocalDate(Long timestamp) {
        if (isZeroTimestamp(timestamp)) {
            return null;
        }
        if (isSecondTimestamp(timestamp)) {
            timestamp = timestamp * 1000;
        }
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.of("+8")).toLocalDate();
    }

    /**
     * 时间戳转 Date
     *
     * @param timestamp
     * @return
     */
    public static Date toDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        if (isSecondTimestamp(timestamp)) {
            timestamp = timestamp * 1000;
        }
        return new Date(timestamp);
    }

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime
     * @return
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.of("+8"));
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    public static String getPreMonthYYYYMM(){
        YearMonth yearMonth = YearMonth.now().minusMonths(1);
        return yearMonth.format(dtyyyyMM.get());
    }

    /**
     * 判断 date1 小于 date2 是否超过的年数
     * @param date1 日期1
     * @param date2 日期2
     * @param numYear 年
     * @return 返回结果
     */
    public static boolean DateCompareYear(Date date1, Date date2, int numYear){
        Date date3 = add(date1, Calendar.YEAR, numYear);
        if (date3.getTime() < date2.getTime()){
            return true;
        }
        return false;
    }

    /**
     * 时间加减
     * @param date
     * @param calendarField Calendar.YEAR / Calendar.MONTH / Calendar.DAY
     * @param amount
     * @return
     */
    public static Date add(Date date, int calendarField, int amount){
        if (date == null){
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static boolean isBefore(LocalDateTime date1, LocalDateTime date2) {
        if (null == date1) {
            return false;
        }
        if (null == date2) {
            return true;
        }
        return date1.isBefore(date2);
    }

    public boolean isAfter(LocalDateTime date1, LocalDateTime date2) {
        if (null == date1) {
            return false;
        }
        if (null == date2) {
            return true;
        }
        return date1.isAfter(date2);
    }

}
