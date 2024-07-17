/*
 * Licensed to the Wiseco Software Corporation under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wiseco.var.process.app.server.commons.util;

import com.wiseco.var.process.app.server.commons.constant.ConstantNumbers;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * 日期时间工具类
 */
public class DateUtil {

    static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    public static final String FORMAT_SHORT1           = "dd";

    public static final String FORMAT_SHORT2           = "yyyyMMdd";

    public static final String FORMAT_SHORT3           = "yyyy";

    public static final String FORMAT_SHORT_YM         = "yyyyMM";

    public static final String FORMAT_SHORT_YM2        = "yyyy-MM";

    public static final String FORMAT_T                = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String FORMAT_UTC              = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";

    public static final String FORMAT_SHORT            = "yyyy-MM-dd";

    public static final String FORMAT_SHORT4           = "yyyy/MM/dd";

    public static final String FORMAT_SHORTEST         = "MM-dd";

    public static final String FORMAT_LONG             = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_LONG4            = "yyyy/MM/dd HH:mm:ss";

    public static final String FORMAT_MINUTE           = "yyyy-MM-dd HH:mm";

    public static final String FORMAT_LONG_0           = "yyyy-MM-dd 00:00:00";

    public static final String FORMAT_LONG2            = "yyyyMMddHHmmss";

    public static final String FORMAT_LONG3            = "yyyyMMddHHmmssSSS";

    public static final String FORMAT_TIME             = "HH_mm_ss_SSS";

    public static final String FORMAT_FULL             = "yyyy-MM-dd HH:mm:ss.S";

    public static final String FORMAT_FULL2            = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String FORMAT_SHORT_CN         = "yyyy年MM月dd日";

    public static final String FORMAT_LONG_CN          = "yyyy年MM月dd日 HH时mm分ss秒";

    public static final String FORMAT_FULL_CN          = "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒";

    public static final String FORMAT_SHORT_ZN         = "MM月dd日HH时mm分";

    public static final String FORMAT_SPECIAL_ZN       = "yyyy年MM月dd HH:mm";

    /**
     * 1 hour == 3,600,000 ms
     */
    public static final Long   MILLISECOND_IN_ONE_HOUR = 3600000L;
    public static final int SECONDS_PER_MINUTE = 60;

    /**
     * 获得默认的 date pattern
     * @return "yyyy-MM-dd HH:mm:ss"
     */
    public static String getDatePattern() {
        return FORMAT_LONG;
    }

    /**
     * 获得短日期 date pattern
     * @return "yyyy-MM-dd"
     */
    public static String getShortDatePattern() {
        return FORMAT_SHORT;
    }

    /**
     * 获得中文短日期 date pattern
     * @return "yyyy年MM月dd日"
     */
    public static String getShortDateCnPattern() {
        return FORMAT_SHORT_CN;
    }

    /**
     * 获得中文短日期MM月dd日HH时mm分
     * @return "MM月dd日HH时mm分"
     */
    public static String getShortDateZnPattern() {
        return FORMAT_SHORT_ZN;
    }

    /**
     * getDateTimeFormatter
     * @param pattern pattern字符串
     * @return DateTimeFormatter
     */
    public static DateTimeFormatter getDateTimeFormatter(String pattern) {
        return DateTimeFormat.forPattern(pattern);
    }

    /**
     * 字符串按指定格式转化为date
     *
     * @param timeStr String
     * @param pattern 格式
     * @return Date
     */
    public static Date parseStrToDate(String timeStr, String pattern) {
        return DateTime.parse(timeStr, getDateTimeFormatter(pattern)).toDate();
    }

    /**
     * date按照指定格式转化为str
     *
     * @param date Date
     * @param pattern 格式
     * @return String
     */
    public static String parseDateToStr(Date date, String pattern) {
        return new DateTime(date).toString(pattern);
    }

    /**
     * 当前日期加数年
     *
     * @param date 当前日期
     * @param num 加几年
     * @return 加完后
     */
    public static Date addYears(Date date, int num) {
        return new DateTime(date).plusYears(num).toDate();
    }

    /**
     * 当前日期加数月
     *
     * @param date 当前日期
     * @param num 加几个月
     * @return 加完后
     */
    public static Date addMonths(Date date, int num) {
        return new DateTime(date).plusMonths(num).toDate();
    }

    /**
     * 当前日期加数日
     *
     * @param date 当前日期
     * @param num 加几天
     * @return 加完后
     */
    public static Date addDays(Date date, int num) {
        return new DateTime(date).plusDays(num).toDate();
    }

    /**
     * 当前日期加数月
     *
     * @param date 当前日期
     * @param num 加几个月
     * @return 加完后
     */
    public static String addMonths(String date, int num) {
        return new DateTime(date).plusMonths(num).toString(FORMAT_SHORT);
    }

    /**
     * 当前日期加数日
     *
     * @param date 当前日期
     * @param num 加几天
     * @return 加完后
     */
    public static String addDays(String date, int num) {
        return new DateTime(date).plusDays(num).toString(FORMAT_SHORT);
    }

    /**
     * 当前日期加数小时
     *
     * @param date 当前日期
     * @param num 加几个小时
     * @return 加完后
     */
    public static Date addHours(Date date, int num) {
        return new DateTime(date).plusHours(num).toDate();
    }

    /**
     * 当前日期加数分钟
     * @param date 当前日期
     * @param num 加几分钟
     * @return 加完后
     */
    public static Date addMinutes(Date date, int num) {
        return new DateTime(date).plusMinutes(num).toDate();
    }

    /**
     * 当前日期加数秒钟
     *
     * @param date 当前日期
     * @param num 加几秒
     * @return 加完后
     */
    public static Date addSeconds(Date date, int num) {
        return new DateTime(date).plusSeconds(num).toDate();
    }

    /**
     * 计算两日期间天数差
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 天数差
     */
    public static int getDays(Object beginDate, Object endDate) {
        return Days.daysBetween(new LocalDate(beginDate), new LocalDate(endDate)).getDays();
    }

    /**
     * 计算两日期间月数差
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 月数差
     */
    public static int getMonths(Object beginDate, Object endDate) {
        return Months.monthsBetween(new LocalDate(beginDate), new LocalDate(endDate)).getMonths();
    }

    /**
     * 计算两日期间分钟差
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 分钟差
     */
    public static int getMinutes(Object beginDate, Object endDate) {
        return Minutes.minutesBetween(new LocalDate(beginDate), new LocalDate(endDate)).getMinutes();
    }

    /**
     * 计算两日期间秒差
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 秒差
     */
    public static long getSeconds(Date beginDate, Date endDate) {
        long seconds = (endDate.getTime() - beginDate.getTime()) / ConstantNumbers.INT_1000;
        return seconds;
    }

    /**
     * 计算两日期间毫秒差
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return long
     */
    public static long getMillis(Date beginDate, Date endDate) {
        return endDate.getTime() - beginDate.getTime();
    }

    /**
     * 根据预设格式返回当前日期
     *
     * @return String
     */
    public static String getNow() {
        return new DateTime().toString(FORMAT_SHORT);
    }

    /**
     * 根据用户格式返回当前日期
     *
     * @param format String
     * @return String
     */
    public static String getNow(String format) {
        return new DateTime().toString(format);
    }

    /**
     * 获取当前日期时间
     *
     * @return Date
     */
    public static Date getNowDate() {
        return new DateTime().toDate();
    }

    /**
     * 获取当前日期时间
     * @param pattern 格式
     * @return Date
     */
    public static Date getNowDate(String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(getNow(FORMAT_LONG));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取当前日期时间Time值
     *
     * @return long
     */
    public static long getNowTime() {

        return new DateTime().getMillis();
    }

    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return Date
     */
    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 计算两日期间分钟差
     *
     * @param d1 Date
     * @param d2 Date
     * @return 分钟差
     */
    public static BigDecimal getMinutes(Date d1, Date d2) {
        // 毫秒ms
        long diff = d2.getTime() - d1.getTime();
        float diffMinutes = (float) diff / (SECONDS_PER_MINUTE * ConstantNumbers.INT_1000);
        return BigDecimal.valueOf(diffMinutes);
    }

    /**
     * 字符串按指定格式转化为date
     * @param timeStr String
     * @return String
     * @throws Exception 异常
     */
    public static String parseUtcStrToStr(String timeStr) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_UTC);
        //注意是空格+UTC
        Date date = format.parse(timeStr.replace("Z", " UTC"));
        return new DateTime(date).toString(FORMAT_LONG);
    }

    /**
     * 当前日期加数月
     *
     * @param date String
     * @param num int
     * @param pattern String
     * @return String
     */
    public static String addMonthsForGyjf(String date, int num, String pattern) {
        String startDate = new DateTime(date).plusMonths(num).toString(pattern);
        return DateUtil.addDays(startDate, ConstantNumbers.MINUS_INT_1);
    }

    /**
     * 判断当前时间是否在给定时间段内
     */
    private static final String         FORMAT_STR = "HH:mm";
    private static final FastDateFormat SDF        = FastDateFormat.getInstance(FORMAT_STR);

    /**
     * isInZone
     * @param startTime String
     * @param endTime String
     * @return boolean
     * @throws ParseException 异常
     */
    public static boolean isInZone(String startTime, String endTime) throws ParseException {
        String pattern = getDateFormat(startTime);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        long tStart = sdf.parse(startTime).getTime();
        long tEnd = sdf.parse(endTime).getTime();
        long t = sdf.parse(sdf.format(new Date())).getTime();
        if (tStart > tEnd) {
            //只有跑批时间的起始时间可以大于结束时间
            if (FORMAT_STR.equals(pattern)) {
                return tStart <= t || t <= tEnd;
            } else {
                return false;
            }
        } else if (tStart < tEnd) {
            return tStart <= t && t <= tEnd;
        } else {
            return t == tStart;
        }
    }

    /**
     * getLong
     * @param timeStr String
     * @return long
     * @throws ParseException 异常
     */
    public static long getLong(String timeStr) throws ParseException {
        return SDF.parse(timeStr).getTime();
    }

    /**
     * getCurrentTime
     * @return long
     * @throws ParseException 异常
     */
    public static long getCurrentTime() throws ParseException {
        return getLong(SDF.format(new Date()));
    }

    /**
     * getSecondsNextEarlyMorning
     * @return Long
     */
    public static Long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / ConstantNumbers.INT_1000;
    }

    /**
     * 按格式转化成对应时间
     *
     * @param timeStamp 时间戳
     * @param format 格式
     * @return String
     */
    public static String timeStampToStr(Long timeStamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // 时间戳转换成时间
        return sdf.format(new Date(timeStamp));
    }

    /**
     * 时间戳格式转化成对应时间
     *
     * @param timeStamp 时间戳
     * @param format 格式
     * @return Date
     */
    public static Date timeStampToDate(Long timeStamp, String format) {
        // 时间戳转换成时间
        Date date = new Date(timeStamp);
        if (format != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.format(date);
        }
        return date;
    }

    /**
     * 常规自动日期格式识别
     *
     * @param str 时间字符串
     * @return Date
     */
    private static String getDateFormat(String str) {
        boolean year = false;
        Pattern pattern = compile("^[-\\+]?[\\d]*$");
        int num = ConstantNumbers.INT_4;
        if (pattern.matcher(str.substring(0, num)).matches()) {
            year = true;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        if (!year) {
            String mouth = "月";
            String heng = "-";
            String xie = "/";
            if (str.contains(mouth) || str.contains(heng) || str.contains(xie)) {
                if (Character.isDigit(str.charAt(0))) {
                    index = 1;
                }
            } else {
                index = ConstantNumbers.INT_3;
            }
        }
        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);
            if (Character.isDigit(chr)) {
                if (index == 0) {
                    sb.append("y");
                }
                if (index == 1) {
                    sb.append("M");
                }
                if (index == ConstantNumbers.INT_2) {
                    sb.append("d");
                }
                if (index == ConstantNumbers.INT_3) {
                    sb.append("H");
                }
                if (index == ConstantNumbers.INT_4) {
                    sb.append("m");
                }
                if (index == ConstantNumbers.INT_5) {
                    sb.append("s");
                }
                if (index == ConstantNumbers.INT_6) {
                    sb.append("S");
                }
            } else {
                if (i > 0) {
                    char lastChar = str.charAt(i - 1);
                    if (Character.isDigit(lastChar)) {
                        index++;
                    }
                }
                sb.append(chr);
            }
        }
        return sb.toString();
    }

    /**
     * 获取下月指定日
     * @param day 日
     * @return 下月指定日
     */
    public static Date getNextMonthDay(String day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        String parseDate = format.format(getNextMonth()) + day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            return sdf.parse(parseDate);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取下个月
     *
     * @return Date
     */
    public static Date getNextMonth() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    /**
     * stringToDateYmd
     * @param ymdStr String
     * @return Date
     * @throws ParseException 转换异常
     */
    public static Date stringToDateYmd(String ymdStr) throws ParseException {
        if (ymdStr != null) {
            DateFormat ymdFormat = new SimpleDateFormat(FORMAT_SHORT2);
            return ymdFormat.parse(ymdStr);
        }
        return null;
    }

    /**
     * 格式化
     * @param date Date
     * @return Date
     */
    public static Date formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_SHORT);
        String format = sdf.format(date);
        return parseStrToDate(format, FORMAT_SHORT);
    }

    /**
     * getDay
     * @param date String
     * @return Integer
     * @throws ParseException 异常
     */
    public static Integer getDay(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_SHORT);
        Calendar calendar = sdf.getCalendar();
        return calendar.get(Calendar.DATE);
    }

    /**
     * 日期比较返回最大日期
     * @param date1 Date
     * @param date2 Date
     * @return 最大日期
     */
    public static Date max(Date date1, Date date2) {
        if (Objects.isNull(date1) || Objects.isNull(date2)) {
            return null;
        }
        int compare = date1.compareTo(date2);
        if (compare > 0) {
            return date1;
        }
        return date2;
    }

    /**
     * 日期比较返回最小日期
     * @param date1 Date
     * @param date2 Date
     * @return 最小日期
     */
    public static Date min(Date date1, Date date2) {
        if (Objects.isNull(date1) || Objects.isNull(date2)) {
            return null;
        }
        int compare = date1.compareTo(date2);
        if (compare < 0) {
            return date1;
        }
        return date2;
    }

    /**
     * 识别数据类型并进行转换
     * @param obj Object
     * @return Date
     */
    public static Date parseInstanceof(Object obj) {
        if (null == obj) {
            return null;
        }
        Date result;
        if (obj instanceof String) {
            if (obj.toString().length() == ConstantNumbers.INT_4) {
                result = parse(obj.toString(), DateUtil.FORMAT_SHORT3);
            } else if (obj.toString().length() == ConstantNumbers.INT_7) {
                result = parse(obj.toString(), DateUtil.FORMAT_SHORT_YM2);
            } else {
                result = cn.hutool.core.date.DateUtil.parse(obj.toString());
            }
        } else if (obj instanceof Long) {
            result = cn.hutool.core.date.DateUtil.date((long) obj);
        } else {
            result = (Date) obj;
        }
        return result;
    }

    /**
     * 向后取整日期时间至下一小时
     *
     * @param date 取整前时间
     * @return 取整后时间
     */
    public static Date roundToTheNearestHour(Date date) {
        // 判断当前时间是否为整点
        long dateInEpochMillis = date.getTime();
        if (0 == dateInEpochMillis % MILLISECOND_IN_ONE_HOUR) {
            // 整点: 直接返回 date
            return date;
        } else {
            // 非整点: 去除余数, 向后取整一小时
            long numberOfHours = (dateInEpochMillis / MILLISECOND_IN_ONE_HOUR) + 1;
            return new Date(numberOfHours * MILLISECOND_IN_ONE_HOUR);
        }
    }

    /**
     * 获取当天日期往前往后n天日期
     * @param n int
     * @return 往后n天日期
     */
    public static String postBeforeDays(int n) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.DATE, -n);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_LONG);
        return sdf.format(calendar.getTime());

    }

    /**
     * 获取结束天数
     *
     * @param date 日期
     * @return Date 时间
     * @throws ParseException 转换异常
     */
    public static Date getEndOfDay(Date date) throws ParseException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new Date(dateFormat.parse(dateFormat.format(date) + " 23:59:59").getTime());
    }

    /**
     * 获取下一天
     * @param date 日期
     * @return 下一天的日期
     */
    public static String getNextDay(Date date) {

        // 使用 Calendar 类来操作日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        // 返回下一天的日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

}
