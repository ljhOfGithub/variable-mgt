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
package com.wiseco.var.process.app.server.commons.test;

import com.apifan.common.random.source.AreaSource;
import com.apifan.common.random.source.DateTimeSource;
import com.apifan.common.random.source.InternetSource;
import com.apifan.common.random.source.OtherSource;
import com.apifan.common.random.source.PersonInfoSource;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import org.apache.commons.lang3.RandomUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * 随机数据生成工具类
 * 
 * @author wangxianli
 * @author Zhaoxiong Chen
 * @since 2021/11/10
 */
public final class RandomGeneratorUtil {

    /**
     * 浮点数格式: 保留小数点后2位
     */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private static SecureRandom secureRandom = new SecureRandom();

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            secureRandom = new SecureRandom();
        }
    }

    /**
     * 工具类禁止初始化
     */
    private RandomGeneratorUtil() {

    }

    /**
     * 生成1个姓名
     * 
     * @return 随机姓名
     */
    public static String generateName() {
        return PersonInfoSource.getInstance().randomChineseName();
    }

    /**
     * 生成1个身份证号
     * 
     * @return 随机身份证号
     */
    public static String generateIdNo() {
        String province = AreaSource.getInstance().randomProvince();

        return PersonInfoSource.getInstance().randomMaleIdCard(province, MagicNumbers.EIGHTEEN, MagicNumbers.INT_60);
    }

    /**
     * 生成1个中国大陆手机号, 11位
     * 
     * @return 随机中国大陆手机号
     */
    public static String generateMobilePhone() {
        return PersonInfoSource.getInstance().randomChineseMobile();
    }

    /**
     * 生成1个email地址
     * 
     * @return 随机email地址
     */
    public static String generateEmail() {
        return InternetSource.getInstance().randomEmail(MagicNumbers.TEN);
    }

    /**
     * 生成1个公司名
     * 
     * @return 随机公司名称
     */
    public static String generateCompanyName() {
        String prv = AreaSource.getInstance().randomProvince();
        return OtherSource.getInstance().randomCompanyName(prv);
    }

    /**
     * 生成1个 UUID
     * 
     * @return UUID
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成1个中国大陆详细地址
     * 
     * @return 随机中国大陆详细地址
     */
    public static String generateAddress() {
        return AreaSource.getInstance().randomAddress();
    }

    /**
     * <p>
     * 生成指定范围的随机日期
     * </p>
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 随机日期 String
     */
    public static String generateRandomDate(LocalDate startDate, LocalDate endDate) {
        return DateTimeSource.getInstance().randomDate(startDate, endDate, "yyyy-MM-dd");
    }

    /**
     * <p>
     * 生成指定范围的随机日期<br>
     * 自动转换输入的字符串类型
     * </p>
     * 
     * @param startDateString 开始日期 String
     * @param endDateString 结束日期 String
     * @return 随机日期 String
     */
    public static String generateRandomDate(String startDateString, String endDateString) {
        String[] startDateSplit = startDateString.split("-");
        String[] endDateSplit = endDateString.split("-");
        LocalDate startDate =
            LocalDate.of(Integer.parseInt(startDateSplit[0]), Integer.parseInt(startDateSplit[1]),
                Integer.parseInt(startDateSplit[MagicNumbers.TWO]));
        LocalDate endDate =
            LocalDate.of(Integer.parseInt(endDateSplit[0]), Integer.parseInt(endDateSplit[1]),
                Integer.parseInt(endDateSplit[MagicNumbers.TWO]));
        return generateRandomDate(startDate, endDate);
    }

    /**
     * <p>
     * 生成指定范围的随机日期时间
     * </p>
     * 
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 随机日期时间 String
     */
    public static String generateRandomDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long time = DateTimeSource.getInstance().randomTimestamp(startDateTime, endDateTime);
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * <p>
     * 生成指定范围的随机日期时间<br>
     * 自动转换输入的字符串类型
     * </p>
     * 
     * @param startDateTimeString 开始日期时间 String
     * @param endDateTimeString 结束日期时间 String
     * @return 随机日期时间 String
     */
    public static String generateRandomDateTime(String startDateTimeString, String endDateTimeString) {
        String[] startSplit = startDateTimeString.replace(" ", "-").replace(":", "-").split("-");
        String[] endSplit = endDateTimeString.replace(" ", "-").replace(":", "-").split("-");

        LocalDateTime startDateTime =
            LocalDateTime.of(Integer.parseInt(startSplit[0]), Integer.parseInt(startSplit[1]),
                Integer.parseInt(startSplit[MagicNumbers.TWO]), Integer.parseInt(startSplit[MagicNumbers.THREE]),
                Integer.parseInt(startSplit[MagicNumbers.FOUR]), Integer.parseInt(startSplit[MagicNumbers.FIVE]));

        LocalDateTime endDateTime =
            LocalDateTime.of(Integer.parseInt(endSplit[0]), Integer.parseInt(endSplit[1]),
                Integer.parseInt(endSplit[MagicNumbers.TWO]), Integer.parseInt(endSplit[MagicNumbers.THREE]),
                Integer.parseInt(endSplit[MagicNumbers.FOUR]), Integer.parseInt(endSplit[MagicNumbers.FIVE]));

        return generateRandomDateTime(startDateTime, endDateTime);
    }

    /**
     * <p>
     * 生成指定区间的随机整数
     * </p>
     * 
     * <p>
     * 随机数区间: [lowerBound, upperBound)
     * </p>
     * 
     * @param lowerBound 随机数下界 int
     * @param upperBound 随机数上界 int
     * @return 随机整数 String
     */
    public static String generateRandomInteger(int lowerBound, int upperBound) {
        long random = RandomUtils.nextInt(lowerBound, upperBound + 1);

        return String.valueOf(random);
    }

    /**
     * <p>
     * 生成指定区间的随机整数<br>
     * 自动转换输入的字符串类型
     * </p>
     * 
     * <p>
     * 随机数区间: [lowerBound, upperBound)
     * </p>
     * 
     * @param lowerBoundString 随机数下界 String
     * @param upperBoundString 随机数上界 String
     * @return 随机整数 String
     */
    public static String generateRandomInteger(String lowerBoundString, String upperBoundString) {
        return generateRandomInteger(Integer.parseInt(lowerBoundString), Integer.parseInt(upperBoundString) + 1);
    }

    /**
     * <p>
     * 生成指定区间的随机浮点数
     * </p>
     * 
     * <p>
     * 随机数区间: [lowerBound, upperBound)
     * </p>
     * 
     * @param lowerBound 随机数下界 double
     * @param upperBound 随机数上界 double
     * @return String 随机浮点数(保留小数点后2位)
     */
    public static String generateRandomDouble(double lowerBound, double upperBound) {
        double random = lowerBound + secureRandom.nextDouble() * (upperBound - lowerBound);

        return DECIMAL_FORMAT.format(random);
    }

    /**
     * <p>
     * 生成指定区间的随机浮点数<br>
     * 自动转换输入的字符串类型
     * </p>
     * 
     * <p>
     * 随机数区间: [lowerBound, upperBound)
     * </p>
     * 
     * @param lowerBoundString 随机数下界 String
     * @param upperBoundString 随机数上界 String
     * @return 随机浮点数(保留小数点后2位) String
     */
    public static String generateRandomDouble(String lowerBoundString, String upperBoundString) {
        return generateRandomDouble(Double.parseDouble(lowerBoundString), Double.parseDouble(upperBoundString));
    }

}
