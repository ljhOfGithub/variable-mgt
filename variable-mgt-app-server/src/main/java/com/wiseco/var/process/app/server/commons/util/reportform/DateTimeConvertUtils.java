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
package com.wiseco.var.process.app.server.commons.util.reportform;

import com.wiseco.boot.commons.util.DateTimeUtils;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;

/**
 * 监控报表专用的时间工具类
 */

public class DateTimeConvertUtils {

    /**
     * 获取以小时为单位的分隔区间
     * @param start 开始时间
     * @param end 结果时间
     * @return 以小时为单位的分隔区间
     */
    public static LinkedHashMap<LocalDateTime, LocalDateTime> getByHour(LocalDateTime start, LocalDateTime end) {
        LinkedHashMap<LocalDateTime, LocalDateTime> result = new LinkedHashMap<>(MagicNumbers.EIGHT);
        // 1.获取开始时间的分钟和秒钟
        int minute = start.getMinute();
        int second = start.getSecond();
        // 2.交替获取时间段
        LocalDateTime t1 = start, t2 = t1.plusHours(MagicNumbers.ONE);
        t2 = t2.minusMinutes(minute);
        t2 = t2.minusSeconds(second + MagicNumbers.ONE);
        int count = MagicNumbers.ZERO;
        while (count < MagicNumbers.TWO) {
            result.put(t1, t2);
            t1 = t2;
            t1 = t1.plusSeconds(MagicNumbers.ONE);
            t2 = t2.plusHours(MagicNumbers.ONE);
            if (t2.isAfter(end) || t2.equals(end)) {
                t2 = end;
                count++;
            }
        }
        return result;
    }

    /**
     * 获取以天为单位的分隔区间
     * @param start 开始时间
     * @param end 结束时间
     * @return 以天为单位的分隔区间
     */
    public static LinkedHashMap<LocalDateTime, LocalDateTime> getByDay(LocalDateTime start, LocalDateTime end) {
        LinkedHashMap<LocalDateTime, LocalDateTime> result = new LinkedHashMap<>(MagicNumbers.EIGHT);
        // 1.获取开始时间的小时,分钟和秒钟
        int hour = start.getHour();
        int minute = start.getMinute();
        int second = start.getSecond();
        // 2.交替获取时间段
        LocalDateTime t1 = start, t2 = t1.plusDays(MagicNumbers.ONE);
        t2 = t2.minusHours(hour);
        t2 = t2.minusMinutes(minute);
        t2 = t2.minusSeconds(second + MagicNumbers.ONE);
        int count = MagicNumbers.ZERO;
        while (count < MagicNumbers.TWO) {
            result.put(t1, t2);
            t1 = t2;
            t1 = t1.plusSeconds(MagicNumbers.ONE);
            t2 = t2.plusDays(MagicNumbers.ONE);
            if (t2.isAfter(end) || t2.equals(end)) {
                t2 = end;
                count++;
            }
        }
        return result;
    }

    /**
     * 获取以星期为单位的分隔区间
     * @param start 开始时间
     * @param end 结束时间
     * @return 以星期为单位的分隔区间
     */
    public static LinkedHashMap<LocalDateTime, LocalDateTime> getByWeek(LocalDateTime start, LocalDateTime end) {
        LinkedHashMap<LocalDateTime, LocalDateTime> result = new LinkedHashMap<>(MagicNumbers.EIGHT);
        // 1.获取开始时间对应的那周的最后一天
        LocalDate localDate = start.toLocalDate();
        LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime weekend = LocalDateTime.of(sunday, LocalTime.MAX);
        // 2.判断是否跨年
        LocalDateTime t1 = start, t2 = null;
        if (weekend.getYear() > t1.getYear()) {
            LocalDate lastDay = t1.toLocalDate().with(TemporalAdjusters.lastDayOfYear());
            t2 = LocalDateTime.of(lastDay, LocalTime.MAX);
        } else {
            t2 = weekend;
        }
        // 3.依次获取时间片
        int count = MagicNumbers.ZERO;
        while (count < MagicNumbers.TWO) {
            result.put(t1, t2);
            t1 = t2;
            t1 = t1.plusSeconds(MagicNumbers.ONE);
            t2 = LocalDateTime.of(t1.toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), LocalTime.MAX);
            // 3.1 继续判断跨年
            if (t2.getYear() > t1.getYear()) {
                LocalDate lastDay = t1.toLocalDate().with(TemporalAdjusters.lastDayOfYear());
                t2 = LocalDateTime.of(lastDay, LocalTime.MAX);
            }
            if (t2.isAfter(end) || t2.equals(end)) {
                t2 = end;
                count++;
            }
        }
        return result;
    }

    /**
     * 获取以月为单位的分隔区间
     * @param start 开始时间
     * @param end 结束时间
     * @return 以月为单位的分隔区间
     */
    public static LinkedHashMap<LocalDateTime, LocalDateTime> getByMonth(LocalDateTime start, LocalDateTime end) {
        LinkedHashMap<LocalDateTime, LocalDateTime> result = new LinkedHashMap<>(MagicNumbers.EIGHT);
        // 1.获取开始时间的年月日
        LocalDate localDate = start.toLocalDate();
        LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
        // 2.交替获取时间
        LocalDateTime t1 = start, t2 = LocalDateTime.of(lastDayOfMonth, LocalTime.MAX);
        int count = MagicNumbers.ZERO;
        while (count < MagicNumbers.TWO) {
            result.put(t1, t2);
            // 2.1 确定t1
            t1 = t2;
            t1 = t1.plusSeconds(MagicNumbers.ONE);
            // 2.2 确定t2
            LocalDate day = t1.toLocalDate();
            LocalDate with = day.with(TemporalAdjusters.lastDayOfMonth());
            t2 = LocalDateTime.of(with, LocalTime.MAX);
            if (t2.isAfter(end) || t2.equals(end)) {
                t2 = end;
                count++;
            }
        }
        return result;
    }

    /**
     * 根据小时获取横轴(纵轴)的字符串
     * @param time 时间对象
     * @return 字符串
     */
    public static String getStringByHour(LocalDateTime time) {
        return DateTimeUtils.parse(time, MagicStrings.DATE_TIME_FORMAT);
    }

    /**
     * 根据周获取横轴(纵轴)的字符串
     * @param time 时间对象
     * @return 字符串
     */
    public static String getStringByWeek(LocalDateTime time) {
        WeekFields weekFields = WeekFields.ISO;
        int num = time.get(weekFields.weekOfYear()) + MagicNumbers.ONE;
        return time.getYear() + MagicStrings.HYPHEN + MagicStrings.NO_NUMBER + num + MagicStrings.WEEK;
    }

    /**
     * 根据天获取横轴(纵轴)的字符串
     * @param time 时间对象
     * @return 字符串
     */
    public static String getStringByDay(LocalDateTime time) {
        return DateTimeUtils.parse(time, MagicStrings.DAY_FORMAT);
    }

    /**
     * 根据月获取横轴(纵轴)的字符串
     * @param time 时间对象
     * @return 字符串
     */
    public static String getStringByMonth(LocalDateTime time) {
        return DateTimeUtils.parse(time, MagicStrings.MONTH_FORMAT);
    }
}
