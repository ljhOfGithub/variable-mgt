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
package com.wiseco.var.process.app.server.commons.util.cron;

import com.wiseco.boot.commons.util.DateTimeUtils;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.ExecuteFrequencyType;
import com.wiseco.var.process.app.server.commons.enums.JobExecuteFrequency;
import com.wiseco.var.process.app.server.commons.enums.TimeUnit;
import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 定时任务的cron工具类
 */
public class ScheduleJobCronUtils {

    public static final int INT_6 = 6;
    public static final int INT_7 = 7;
    public static final int INT_2 = 2;
    public static final int INT_3 = 3;
    public static final int INT_4 = 4;

    public static final int INT_5 = 5;

    private static final String INTERVAL_BASE_NOW_CORN_TEMPLATE = "{0} {1} {2} {3} {4} ? {5}";
    private static final String SPACE = " ";
    private static final Pattern NUM_PATTERN = Pattern.compile("[0-9]{1,4}");
    private static final Map<Integer, String> WEEK_INDEX_TO_EN_MAP = new HashMap<>(MagicNumbers.SIXTEEN);
    private static final Map<String, Integer> WEEK_EN_TO_INDEX_MAP = new HashMap<>(MagicNumbers.SIXTEEN);
    private static final Map<String, Integer> UNIT_VALUE_SCOPE = new HashMap<>(MagicNumbers.SIXTEEN);

    static {
        UNIT_VALUE_SCOPE.put(ChronoUnit.SECONDS.name(), MagicNumbers.INT_59);
        UNIT_VALUE_SCOPE.put(ChronoUnit.MINUTES.name(), MagicNumbers.INT_59);
        UNIT_VALUE_SCOPE.put(ChronoUnit.HOURS.name(), MagicNumbers.INT_23);
        UNIT_VALUE_SCOPE.put(ChronoUnit.DAYS.name(), MagicNumbers.INT_31);
        UNIT_VALUE_SCOPE.put(ChronoUnit.MONTHS.name(), MagicNumbers.TWELVE);
        UNIT_VALUE_SCOPE.put(ChronoUnit.WEEKS.name(), INT_4);
        UNIT_VALUE_SCOPE.put(ChronoUnit.YEARS.name(), MagicNumbers.INT_3K);

        WEEK_INDEX_TO_EN_MAP.put(1, "MON");
        WEEK_INDEX_TO_EN_MAP.put(INT_2, "TUE");
        WEEK_INDEX_TO_EN_MAP.put(INT_3, "WED");
        WEEK_INDEX_TO_EN_MAP.put(INT_4, "THU");
        WEEK_INDEX_TO_EN_MAP.put(INT_5, "FRI");
        WEEK_INDEX_TO_EN_MAP.put(INT_6, "SAT");
        WEEK_INDEX_TO_EN_MAP.put(INT_7, "SUN");

        WEEK_EN_TO_INDEX_MAP.put("MON", 1);
        WEEK_EN_TO_INDEX_MAP.put("TUE", INT_2);
        WEEK_EN_TO_INDEX_MAP.put("WED", INT_3);
        WEEK_EN_TO_INDEX_MAP.put("THU", INT_4);
        WEEK_EN_TO_INDEX_MAP.put("FRI", INT_5);
        WEEK_EN_TO_INDEX_MAP.put("SAT", INT_6);
        WEEK_EN_TO_INDEX_MAP.put("SUN", INT_7);
    }

    /**
     * generateCron
     *
     * @param config 定时任务执行配置
     * @return cron表达式
     */
    public static String generateCron(ScheduleJobExecuteConfig config) {
        StringBuilder cronBuilder = new StringBuilder();
        //时分秒月的处理逻辑完全相同
        //秒
        handlerBaseConfig(cronBuilder, config.getSecondConfig(), ChronoUnit.SECONDS);
        //分
        handlerBaseConfig(cronBuilder, config.getMinuteConfig(), ChronoUnit.MINUTES);
        //时
        handlerBaseConfig(cronBuilder, config.getHourConfig(), ChronoUnit.HOURS);
        //日
        handlerDayConfig(cronBuilder, config.getDayConfig());
        //月
        handlerBaseConfig(cronBuilder, config.getMonthConfig(), ChronoUnit.MONTHS);
        //星期
        handlerWeekConfig(cronBuilder, config.getWeekConfig());
        //年份
        handlerYearConfig(cronBuilder, config.getYearConfig());
        return cronBuilder.toString();
    }

    private static void handlerYearConfig(StringBuilder cronBuilder, CronEveryUnitBaseConfig yearConfig) {
        if (yearConfig == null) {
            //结束
            cronBuilder.replace(cronBuilder.length() - 1, cronBuilder.length(), "");
            return;
        }

        switch (yearConfig.getExecuteFrequencyType()) {
            case NONE:
                cronBuilder.replace(cronBuilder.length() - 1, cronBuilder.length(), "");
                break;
            case EVERY:
                cronBuilder.append(ExecuteFrequencyType.EVERY.getTypeChar());
                break;
            case SCOPE:
                Scope scope = yearConfig.getScope();
                if (scope == null) {
                    throw new InternalDataServiceException("生成cron表达式配置不正确:[" + ChronoUnit.YEARS + "],[scope]");
                }
                cronBuilder.append(scope.getStart()).append(ExecuteFrequencyType.SCOPE.getTypeChar()).append(scope.getEnd());
                break;
            default:
        }
    }

    /**
     * handlerWeekConfig
     *
     * @param cronBuilder
     * @param weekConfig
     */
    private static void handlerWeekConfig(StringBuilder cronBuilder, CronWeekConfig weekConfig) {
        switch (weekConfig.getExecuteFrequencyType()) {
            case NONE:
                cronBuilder.append(ExecuteFrequencyType.NONE.getTypeChar()).append(SPACE);
                break;
            case LAST:
                int lastWeekDayNum = weekConfig.getLastWeekDayNum();
                cronBuilder.append(lastWeekDayNum).append(ExecuteFrequencyType.LAST.getTypeChar()).append(SPACE);
                break;
            case WEEK_TARGET:
                WeekTarget weekTarget = weekConfig.getWeekTarget();
                if (weekTarget == null) {
                    throw new InternalDataServiceException("生成cron表达式配置不正确:[" + ChronoUnit.WEEKS.name() + "],[weekTarget]");
                }
                cronBuilder.append(weekTarget.getWeekIndex()).append(ExecuteFrequencyType.WEEK_TARGET.getTypeChar()).append(weekTarget.getWeekDayNum()).append(SPACE);
                break;
            case TARGET:
                int[] target = weekConfig.getTarget();
                if (target == null) {
                    throw new InternalDataServiceException("生成cron表达式配置不正确:[week],[target]");
                }
                //这里需要将数字1~7转为英文描述简写 对应星期一到星期日
                if (target.length == 1) {
                    cronBuilder.append(WEEK_INDEX_TO_EN_MAP.get(target[0])).append(SPACE);
                } else {
                    List<Integer> list = new ArrayList<>();
                    for (int i = 0; i < target.length; i++) {
                        list.add(target[i]);
                    }
                    list.stream().distinct().sorted().collect(Collectors.toList()).forEach(targetNum -> {
                        cronBuilder.append(WEEK_INDEX_TO_EN_MAP.get(targetNum)).append(ExecuteFrequencyType.TARGET.getTypeChar());
                    });
                    cronBuilder.replace(cronBuilder.length() - 1, cronBuilder.length(), SPACE);
                }
                break;
            default:
                handlerBaseConfig(cronBuilder, weekConfig, ChronoUnit.DAYS);
        }
    }

    /**
     * handlerDayConfig
     *
     * @param cronBuilder
     * @param dayConfig
     */
    private static void handlerDayConfig(StringBuilder cronBuilder, CronDayConfig dayConfig) {
        switch (dayConfig.getExecuteFrequencyType()) {
            case NONE:
            case LAST:
                cronBuilder.append(dayConfig.getExecuteFrequencyType().getTypeChar()).append(SPACE);
                break;
            case RECENT_WORK_DAY:
                int recentWorkDayReferenceDay = dayConfig.getRecentWorkDayReferenceDay();
                cronBuilder.append(recentWorkDayReferenceDay).append(ExecuteFrequencyType.RECENT_WORK_DAY.getTypeChar()).append(SPACE);
                break;
            default:
                handlerBaseConfig(cronBuilder, dayConfig, ChronoUnit.DAYS);
        }
    }

    /**
     * handlerBaseConfig
     *
     * @param cronBuilder
     * @param baseConfig
     * @param unit
     */
    private static void handlerBaseConfig(StringBuilder cronBuilder, CronEveryUnitBaseConfig baseConfig, ChronoUnit unit) {
        String cronPart = generateCronPartByBaseConfig(baseConfig, unit);
        if (cronPart != null) {
            cronBuilder.append(cronPart).append(SPACE);
        }
    }

    /**
     * generateCronPartByBaseConfig
     *
     * @param baseConfig
     * @param unit
     * @return java.lang.String
     */
    private static String generateCronPartByBaseConfig(CronEveryUnitBaseConfig baseConfig, ChronoUnit unit) {
        String result = null;
        switch (baseConfig.getExecuteFrequencyType()) {
            case EVERY:
                result = ExecuteFrequencyType.EVERY.getTypeChar();
                break;
            case SCOPE:
                Scope scope = baseConfig.getScope();
                if (scope == null) {
                    throw new InternalDataServiceException("生成cron表达式配置不正确:[" + unit.name() + "],[scope]");
                }
                result = scope.getStart() + ExecuteFrequencyType.SCOPE.getTypeChar();
                break;
            case FIXED_INTERVAL:
                FixedInterval fixedInterval = baseConfig.getFixedInterval();
                if (fixedInterval == null) {
                    throw new InternalDataServiceException("生成cron表达式配置不正确:[" + unit.name() + "],[fixedInterval]");
                }
                result = fixedInterval.getStart() + ExecuteFrequencyType.FIXED_INTERVAL.getTypeChar();
                break;
            case TARGET:
                int[] target = baseConfig.getTarget();
                if (target == null) {
                    throw new InternalDataServiceException("生成cron表达式配置不正确:[" + unit.name() + "],[target]");
                }
                if (target.length == 1) {
                    result = "" + target[0];
                    break;
                } else {
                    List<Integer> list = new ArrayList<>();
                    for (int i = 0; i < target.length; i++) {
                        list.add(target[i]);
                    }
                    StringBuilder cronPartBuilder = new StringBuilder();
                    list.stream().distinct().sorted().collect(Collectors.toList()).forEach(targetNum -> {
                        cronPartBuilder.append(targetNum).append(ExecuteFrequencyType.TARGET.getTypeChar());
                    });
                    cronPartBuilder.replace(cronPartBuilder.length() - 1, cronPartBuilder.length(), "");
                    result = cronPartBuilder.toString();
                    break;
                }
            default:
        }

        return result;
    }

    /**
     * boundaryValueProcess
     *
     * @param current
     * @param min
     * @param max
     * @return int
     */
    private static int boundaryValueProcess(int current, int min, int max) {
        if (min > max) {
            throw new InternalDataServiceException("边界值处理失败：[边界值设置不合理]");
        }
        if (current >= min && current <= max) {
            return current;
        } else if (current < min) {
            return min;
        } else {
            return max;
        }
    }

    /**
     * parseCron
     *
     * @param cron cron表达式
     * @return 定时任务执行配置
     */
    public static ScheduleJobExecuteConfig parseCron(String cron) {
        if (StringUtils.isEmpty(cron)) {
            return null;
        }
        String[] cronPartArr = cron.split(SPACE);
        if (cronPartArr.length != INT_6 && cronPartArr.length != INT_7) {
            //格式有问题
            throw new InternalDataServiceException("cron表达式错误，无法解析");
        }
        ScheduleJobExecuteConfig.ScheduleJobExecuteConfigBuilder builder = ScheduleJobExecuteConfig.builder();
        //秒、分、时、月的解析方式一致
        //秒
        CronEveryUnitBaseConfig secondConfig = parseBaseConfig(cronPartArr[0]);
        builder.secondConfig(secondConfig);
        //分
        CronEveryUnitBaseConfig minuteConfig = parseBaseConfig(cronPartArr[1]);
        builder.minuteConfig(minuteConfig);
        //时
        CronEveryUnitBaseConfig hourConfig = parseBaseConfig(cronPartArr[INT_2]);
        builder.hourConfig(hourConfig);
        //天
        CronDayConfig cronDayConfig = parseDayConfig(cronPartArr[INT_3]);
        builder.dayConfig(cronDayConfig);
        //月
        CronEveryUnitBaseConfig monthConfig = parseBaseConfig(cronPartArr[INT_4]);
        builder.monthConfig(monthConfig);
        //星期
        CronWeekConfig weekConfig = parseWeekConfig(cronPartArr[INT_5]);
        builder.weekConfig(weekConfig);
        //年份属于非必须的
        if (cronPartArr.length == INT_7) {
            CronEveryUnitBaseConfig yearConfig = parseBaseConfig(cronPartArr[INT_6]);
            builder.yearConfig(yearConfig);
        }
        return builder.build();
    }

    private static CronWeekConfig parseWeekConfig(String weekCronPart) {
        CronWeekConfig weekConfig = new CronWeekConfig();
        if (ExecuteFrequencyType.NONE.getTypeChar().equals(weekCronPart)) {
            weekConfig.setExecuteFrequencyType(ExecuteFrequencyType.NONE);
            return weekConfig;
        }
        if (weekCronPart.contains(ExecuteFrequencyType.WEEK_TARGET.getTypeChar())) {
            String[] weekTargetArr = weekCronPart.split(ExecuteFrequencyType.WEEK_TARGET.getTypeChar());
            if (weekTargetArr.length != INT_2) {
                throw new InternalDataServiceException("cron表达式格式错误");
            }
            weekConfig.setExecuteFrequencyType(ExecuteFrequencyType.WEEK_TARGET);
            WeekTarget weekTarget = WeekTarget.builder().weekIndex(Integer.parseInt(weekTargetArr[0])).weekDayNum(Integer.parseInt(weekTargetArr[1]))
                    .build();
            weekConfig.setWeekTarget(weekTarget);
            return weekConfig;
        }
        if (weekCronPart.contains(ExecuteFrequencyType.LAST.getTypeChar())) {
            String replace = weekCronPart.replace(ExecuteFrequencyType.LAST.getTypeChar(), "");
            if (!NUM_PATTERN.matcher(replace).matches()) {
                throw new InternalDataServiceException("cron表达式格式错误");
            }
            weekConfig.setExecuteFrequencyType(ExecuteFrequencyType.LAST);
            weekConfig.setLastWeekDayNum(Integer.parseInt(replace));
            return weekConfig;
        }
        if (weekCronPart.contains(ExecuteFrequencyType.TARGET.getTypeChar()) || WEEK_EN_TO_INDEX_MAP.containsKey(weekCronPart)) {
            weekConfig.setExecuteFrequencyType(ExecuteFrequencyType.TARGET);
            String[] weekDayEnArr = weekCronPart.split(ExecuteFrequencyType.TARGET.getTypeChar());
            int[] targets = new int[weekDayEnArr.length];
            for (int i = 0; i < weekDayEnArr.length; i++) {
                Integer index = WEEK_EN_TO_INDEX_MAP.get(weekDayEnArr[i]);
                targets[i] = index;
            }
            weekConfig.setTarget(targets);
        }
        return weekConfig;
    }

    private static CronDayConfig parseDayConfig(String dayCronPart) {
        CronDayConfig cronDayConfig = new CronDayConfig();

        CronEveryUnitBaseConfig unitBaseConfig = parseBaseConfig(dayCronPart);
        if (unitBaseConfig != null) {
            cronDayConfig.setExecuteFrequencyType(unitBaseConfig.getExecuteFrequencyType());
            cronDayConfig.setScope(unitBaseConfig.getScope());
            cronDayConfig.setFixedInterval(unitBaseConfig.getFixedInterval());
            cronDayConfig.setTarget(unitBaseConfig.getTarget());
            return cronDayConfig;
        }
        if (ExecuteFrequencyType.NONE.getTypeChar().equals(dayCronPart)) {
            cronDayConfig.setExecuteFrequencyType(ExecuteFrequencyType.NONE);
            return cronDayConfig;
        }
        if (dayCronPart.contains(ExecuteFrequencyType.RECENT_WORK_DAY.getTypeChar())) {
            String replace = dayCronPart.replace(ExecuteFrequencyType.RECENT_WORK_DAY.getTypeChar(), "");
            if (!NUM_PATTERN.matcher(replace).matches()) {
                throw new InternalDataServiceException("cron表达式格式错误");
            }
            cronDayConfig.setExecuteFrequencyType(ExecuteFrequencyType.RECENT_WORK_DAY);
            cronDayConfig.setRecentWorkDayReferenceDay(Integer.parseInt(replace));
            return cronDayConfig;
        }
        if (ExecuteFrequencyType.LAST.getTypeChar().equals(dayCronPart)) {
            cronDayConfig.setExecuteFrequencyType(ExecuteFrequencyType.LAST);
            return cronDayConfig;
        }
        return null;
    }

    /**
     * parseBaseConfig
     *
     * @param baseCronPart
     * @return com.wiseco.var.process.app.server.commons.util.cron.CronEveryUnitBaseConfig
     */
    private static CronEveryUnitBaseConfig parseBaseConfig(String baseCronPart) {

        CronEveryUnitBaseConfig.CronEveryUnitBaseConfigBuilder builder = CronEveryUnitBaseConfig.builder();
        if (ExecuteFrequencyType.EVERY.getTypeChar().equals(baseCronPart)) {
            builder.executeFrequencyType(ExecuteFrequencyType.EVERY);
        } else if (ExecuteFrequencyType.NONE.getTypeChar().equals(baseCronPart)) {
            builder.executeFrequencyType(ExecuteFrequencyType.NONE);
        } else if (baseCronPart.contains(ExecuteFrequencyType.SCOPE.getTypeChar())) {
            String[] scopeArr = baseCronPart.split(ExecuteFrequencyType.SCOPE.getTypeChar());
            if (scopeArr.length != INT_2) {
                throw new InternalDataServiceException("cron表达式错误，无法解析");
            }
            builder.executeFrequencyType(ExecuteFrequencyType.SCOPE);
            Scope scope = Scope.builder().start(Integer.parseInt(scopeArr[0])).end(Integer.parseInt(scopeArr[1])).build();
            builder.scope(scope);
        } else if (baseCronPart.contains(ExecuteFrequencyType.FIXED_INTERVAL.getTypeChar())) {
            String[] fixedIntervalArr = baseCronPart.split(ExecuteFrequencyType.FIXED_INTERVAL.getTypeChar());
            if (fixedIntervalArr.length != INT_2) {
                throw new InternalDataServiceException("cron表达式错误，无法解析");
            }
            builder.executeFrequencyType(ExecuteFrequencyType.FIXED_INTERVAL);
            FixedInterval fixedInterval = FixedInterval.builder().start(Integer.parseInt(fixedIntervalArr[0]))
                    .interval(Integer.parseInt(fixedIntervalArr[1])).build();
            builder.fixedInterval(fixedInterval);
        } else if (baseCronPart.contains(ExecuteFrequencyType.TARGET.getTypeChar()) || NUM_PATTERN.matcher(baseCronPart).matches()) {
            String[] targetArr = baseCronPart.split(ExecuteFrequencyType.TARGET.getTypeChar());
            int[] target = new int[targetArr.length];
            for (int i = 0; i < targetArr.length; i++) {
                target[i] = Integer.parseInt(targetArr[i]);
            }

            builder.executeFrequencyType(ExecuteFrequencyType.TARGET);
            builder.target(target);
        } else {
            return null;
        }
        return builder.build();
    }

    /**
     * generateCronByPageConfig
     *
     * @param pageJobExecuteConfig 页面任务执行配置
     * @return cron表达式
     */
    public static String generateCronByPageConfig(PageJobExecuteConfig pageJobExecuteConfig) {
        ScheduleJobExecuteConfig config = transferScheduleJobExecuteConfig(pageJobExecuteConfig);
        if (config == null) {
            throw new InternalDataServiceException("页面任务执行配置错误");
        }
        return generateCron(config);
    }

    /**
     * transferScheduleJobExecuteConfig
     *
     * @param config
     * @return com.wiseco.var.process.app.server.commons.util.cron.ScheduleJobExecuteConfig
     */
    private static ScheduleJobExecuteConfig transferScheduleJobExecuteConfig(PageJobExecuteConfig config) {
        if (config == null || config.getExecuteFrequency() == null) {
            return null;
        }

        ScheduleJobExecuteConfig result;
        switch (config.getExecuteFrequency()) {
            case EVERY_DAY:
                result = getEveryDayTargetTimeExecuteConfig(config.getHourNum(), config.getMinuteNum());
                break;
            case EVERY_MONTH:
                result = getEveryMonthTargetDateTimeExecuteConfig(config.getDayInMonth(), config.getHourNum(), config.getMinuteNum());
                break;
            case TARGET:
                result = getTargetExecuteConfig(config.getTargetMonths(), config.getTargetDays(), config.getHourNum(), config.getMinuteNum());
                break;
            case EVERY_WEEK:
                result = getEveryWeekTargetDateTimeExcuteConfig(config.getDayInWeek(), config.getHourNum(), config.getMinuteNum());
                break;
            case EVERY_QUARTER:
                result = getEveryQuarterTargetDateTimeExcuteConfig(config.getQuarterExecPlans(), config.getHourNum(), config.getMinuteNum());
                break;
            default:
                result = null;
        }
        return result;
    }

    private static ScheduleJobExecuteConfig getEveryQuarterTargetDateTimeExcuteConfig(List<QuarterExecPlan> quarterExecPlans, int hourNum, int minuteNum) {
        List<Integer> targetMonthNumList = quarterExecPlans.stream().map(QuarterExecPlan::getMonthInQuarter).distinct().sorted().collect(Collectors.toList());
        List<Integer> targetDayList = quarterExecPlans.stream().map(QuarterExecPlan::getDayInMonth).distinct().sorted().collect(Collectors.toList());

        int[] targetMonths = new int[targetMonthNumList.size()];
        int[] targetDays = new int[targetDayList.size()];

        for (int i = 0; i < targetMonthNumList.size(); i++) {
            targetMonths[i] = targetMonthNumList.get(i);
        }
        for (int j = 0; j < targetDayList.size(); j++) {
            targetDays[j] = targetDayList.get(j);
        }
        return getTargetExecuteConfig(targetMonths, targetDays, hourNum, minuteNum);
    }

    private static ScheduleJobExecuteConfig getEveryWeekTargetDateTimeExcuteConfig(int dayInWeek, int hourNum, int minuteNum) {
        CronDayConfig cronDayConfig = new CronDayConfig();
        cronDayConfig.setExecuteFrequencyType(ExecuteFrequencyType.NONE);

        CronWeekConfig weekConfig = new CronWeekConfig();
        weekConfig.setExecuteFrequencyType(ExecuteFrequencyType.TARGET);
        weekConfig.setTarget(new int[]{dayInWeek});

        return ScheduleJobExecuteConfig
                .builder()
                .secondConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{0}).build())
                .minuteConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{minuteNum}).build())
                .hourConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{hourNum}).build())
                .dayConfig(cronDayConfig).monthConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.EVERY).build())
                .weekConfig(weekConfig).build();
    }

    private static ScheduleJobExecuteConfig getTargetExecuteConfig(int[] targetMonths, int[] targetDays, int hourNum, int minuteNum) {
        return ScheduleJobExecuteConfig
                .builder()
                .secondConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{0}).build())
                .minuteConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{minuteNum}).build())
                .hourConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{hourNum}).build())
                .dayConfig(new CronDayConfig().setExecuteDay(targetDays))
                .monthConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(targetMonths).build())
                .weekConfig(new CronWeekConfig().getDefault()).build();
    }

    private static ScheduleJobExecuteConfig getEveryMonthTargetDateTimeExecuteConfig(int dayInMonth, int hourNum, int minuteNum) {
        return ScheduleJobExecuteConfig
                .builder()
                .secondConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{0}).build())
                .minuteConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{minuteNum}).build())
                .hourConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{hourNum}).build())
                .dayConfig(new CronDayConfig().setExecuteDay(dayInMonth))
                .monthConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.EVERY).build())
                .weekConfig(new CronWeekConfig().getDefault()).build();
    }

    private static ScheduleJobExecuteConfig getEveryDayTargetTimeExecuteConfig(int hourNum, int minuteNum) {
        return ScheduleJobExecuteConfig
                .builder()
                .secondConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{0}).build())
                .minuteConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{minuteNum}).build())
                .hourConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.TARGET).target(new int[]{hourNum}).build())
                .dayConfig(new CronDayConfig()).monthConfig(CronEveryUnitBaseConfig.builder().executeFrequencyType(ExecuteFrequencyType.EVERY).build())
                .weekConfig(new CronWeekConfig().getDefault()).build();
    }

    /**
     * parseCronForPage
     *
     * @param cron cron表达式
     * @return 页面任务执行配置对象
     */
    public static PageJobExecuteConfig parseCronForPage(String cron) {
        ScheduleJobExecuteConfig config = parseCron(cron);
        return transferPageJobExecuteConfig(config);
    }

    private static PageJobExecuteConfig transferPageJobExecuteConfig(ScheduleJobExecuteConfig config) {
        PageJobExecuteConfig.PageJobExecuteConfigBuilder builder = PageJobExecuteConfig.builder();
        CronEveryUnitBaseConfig minuteConfig = config.getMinuteConfig();
        if (minuteConfig.getExecuteFrequencyType() == ExecuteFrequencyType.TARGET) {
            builder.minuteNum(minuteConfig.getTarget()[0]);
        }
        CronEveryUnitBaseConfig hourConfig = config.getHourConfig();
        if (hourConfig.getExecuteFrequencyType() == ExecuteFrequencyType.TARGET) {
            builder.hourNum(hourConfig.getTarget()[0]);
        }
        if (config.getDayConfig().getExecuteFrequencyType() == ExecuteFrequencyType.EVERY) {
            builder.executeFrequency(JobExecuteFrequency.EVERY_DAY);
            return builder.build();
        } else if (config.getMonthConfig().getExecuteFrequencyType() == ExecuteFrequencyType.EVERY
                && config.getWeekConfig().getExecuteFrequencyType() == ExecuteFrequencyType.NONE) {
            builder.executeFrequency(JobExecuteFrequency.EVERY_MONTH);
            CronDayConfig dayConfig = config.getDayConfig();
            if (dayConfig.getExecuteFrequencyType() == ExecuteFrequencyType.TARGET) {
                builder.dayInMonth(dayConfig.getTarget()[0]);
            }
            return builder.build();
        } else if (config.getDayConfig().getExecuteFrequencyType() == ExecuteFrequencyType.NONE
                && config.getWeekConfig().getExecuteFrequencyType() == ExecuteFrequencyType.TARGET) {
            builder.executeFrequency(JobExecuteFrequency.EVERY_WEEK);
            CronWeekConfig weekConfig = config.getWeekConfig();
            builder.dayInWeek(weekConfig.getTarget()[0]);
            return builder.build();
        } else {
            CronDayConfig dayConfig = config.getDayConfig();
            if (dayConfig.getExecuteFrequencyType() == ExecuteFrequencyType.TARGET) {
                builder.targetDays(dayConfig.getTarget());
            }
            CronEveryUnitBaseConfig monthConfig = config.getMonthConfig();
            if (monthConfig.getExecuteFrequencyType() == ExecuteFrequencyType.TARGET) {
                builder.targetMonths(monthConfig.getTarget());
            }
            builder.executeFrequency(JobExecuteFrequency.TARGET);
            //通过分析执行月与日判断是否是每季度执行的
            int[] targetMonths = monthConfig.getTarget();
            int[] targetDays = dayConfig.getTarget();
            if (targetMonths.length == INT_4 && targetDays.length == 1) {
                List<QuarterExecPlan> quarterExecPlans = new ArrayList<>();
                for (int i = 0; i < INT_4; i++) {
                    int start = i * MagicNumbers.THREE + 1;
                    int end = (i + 1) * MagicNumbers.THREE;
                    if (targetMonths[i] < start || targetMonths[i] > end) {
                        break;
                    }
                    QuarterExecPlan quarterExecPlan = new QuarterExecPlan(i + 1, targetMonths[i], targetDays[0]);
                    quarterExecPlans.add(quarterExecPlan);
                }
                if (quarterExecPlans.size() == INT_4) {
                    builder.executeFrequency(JobExecuteFrequency.EVERY_QUARTER);
                    builder.quarterExecPlans(quarterExecPlans);
                    builder.targetMonths(null);
                    builder.targetDays(null);
                }
            }
            return builder.build();
        }
    }

    /**
     * 生成下一次任务执行的cron表达式，基于当前时间
     *
     * @param interval 间隔长度
     * @param timeUnit 间隔时间单位
     * @return String
     */
    public static String generateCronBaseNow(Integer interval, TimeUnit timeUnit) {
        LocalDateTime nextRetryDateTime = DateTimeUtils.plus(LocalDateTime.now(), interval, transferChronoUnit(timeUnit));
        //取下次执行日期的秒、分、时、日、月、年
        return MessageFormat.format(INTERVAL_BASE_NOW_CORN_TEMPLATE, nextRetryDateTime.getSecond(), nextRetryDateTime.getMinute(),
                nextRetryDateTime.getHour(), nextRetryDateTime.getDayOfMonth(), nextRetryDateTime.getMonthValue(),
                String.valueOf(nextRetryDateTime.getYear()));
    }

    private static TemporalUnit transferChronoUnit(TimeUnit timeUnit) {
        switch (timeUnit) {
            case HOUR:
                return ChronoUnit.HOURS;
            case MINUTE:
                return ChronoUnit.MINUTES;
            case SECOND:
                return ChronoUnit.SECONDS;
            default:
                return null;
        }
    }
}
