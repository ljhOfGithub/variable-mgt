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
package com.wiseco.var.process.app.server.commons.test.autogentypes;

import com.wiseco.var.process.app.server.enums.GenerateCustomEnum;
import com.wiseco.var.process.app.server.commons.test.RandomGeneratorUtil;
import com.wiseco.var.process.app.server.commons.test.TestDataAutoGenerator;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 自定义类 测试数据在线自动生成器
 * <p>
 * 生成规则示例:
 * generateUUID|0.88;nul|0.12
 *
 * @author Zhaoxiong Chen
 * @see GenerateCustomEnum 自定义数据生成类型
 * @since 2022/1/7
 */
public class CustomTypeAutoGenerator implements TestDataAutoGenerator {

    /**
     * 流水号前缀切换上限
     */
    private static final int PREFIX_SWITCH_LIMIT = 10000000;
    /**
     * 流水号前缀切换上限
     */
    private static final int SN_PREFIX_SWITCH_LIMIT = 10000000;
    /**
     * 流水号前缀初始值
     */
    private static final char SN_PREFIX_INITIAL_VALUE = 'A';
    /**
     * 表示流水号前缀需要更换的指示器
     * i.e. ASCII 表中 'Z' 的下一个字符
     * A, B, ..., Z, [
     */
    private static final char SN_PREFIX_CHANGE_INDICATOR = '[';
    private static SecureRandom secureRandom = new SecureRandom();

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            secureRandom = new SecureRandom();
        }
    }

    /**
     * 自定义类别
     */
    private final String customizedCategory;
    /**
     * 空值概率
     */
    private final double nullProbability;
    /**
     * 流水号前缀
     */
    private char serialNumberPrefix;
    /**
     * 流水号计数器
     */
    private int serialNumberCounter;

    /**
     * CustomTypeAutoGenerator
     *
     * @param generationRuleFormula 生成规则公式
     */
    public CustomTypeAutoGenerator(String generationRuleFormula) {
        // customizedCategory|1-nullProbability;nul|nullProbability
        // 按 ";" 分割所有枚举选项的生成规则
        String[] generationRules = generationRuleFormula.split(";");

        // 自定义类别: customizedCategory|1-nullProbability
        this.customizedCategory = generationRules[0].split("\\|")[0];
        // 空值概率: nul|nullProbability
        this.nullProbability = Double.parseDouble(generationRules[1].split("\\|")[1]);

        // 随机生成流水号前缀
        this.serialNumberPrefix = SN_PREFIX_INITIAL_VALUE;
        // 重置流水号计数器
        this.serialNumberCounter = 0;
    }

    @Override
    public String getValue() {
        double randomDouble = secureRandom.nextDouble();

        String result;
        if (randomDouble < nullProbability) {
            // 空值
           result =  "";
        } else {
            if (GenerateCustomEnum.NAME.getCode().equals(customizedCategory)) {
                // 生成姓名
                result = RandomGeneratorUtil.generateName();
            } else if (GenerateCustomEnum.IDNO.getCode().equals(customizedCategory)) {
                // 生成身份证号
                result = RandomGeneratorUtil.generateIdNo();
            } else if (GenerateCustomEnum.MOBILE.getCode().equals(customizedCategory)) {
                // 生成手机号
                result = RandomGeneratorUtil.generateMobilePhone();
            } else if (GenerateCustomEnum.EMAIL.getCode().equals(customizedCategory)) {
                // 生成邮箱
                result = RandomGeneratorUtil.generateEmail();
            } else if (GenerateCustomEnum.COMPANY.getCode().equals(customizedCategory)) {
                // 生成公司名称
                result = RandomGeneratorUtil.generateCompanyName();
            } else if (GenerateCustomEnum.SERIAL_NUMBER.getCode().equals(customizedCategory)) {
                // 生成流水号
                result = this.generateSerialNumber();
            } else if (GenerateCustomEnum.ADDRESS.getCode().equals(customizedCategory)) {
                // 生成详细地址
                result = RandomGeneratorUtil.generateAddress();
            } else {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"不支持的自定义生成类型");
            }
        }

        return result;
    }

    private String generateSerialNumber() {
        if (serialNumberCounter >= SN_PREFIX_SWITCH_LIMIT - 1) {
            // 流水号数字部分超过上界
            // 重置流水号计数器
            serialNumberCounter = 1;
            // 变更流水号前缀
            serialNumberPrefix += 1;
            if (serialNumberPrefix == SN_PREFIX_CHANGE_INDICATOR) {
                serialNumberPrefix = SN_PREFIX_INITIAL_VALUE;
            }
        } else {
            serialNumberCounter++;
        }

        return serialNumberPrefix + String.format("%07d", serialNumberCounter);
    }
}
