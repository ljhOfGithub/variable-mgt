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

import com.wiseco.var.process.app.server.commons.test.TestDataAutoGenerator;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 随机类型 测试数据在线自动生成器
 * <p>
 * 生成规则示例:
 * 下界,上界|1-空值概率;nul|空值概率
 *
 * @param <T> 随机数据类型: Integer, Double, LocalDate, LocalDateTime
 * @author Zhaoxiong Chen
 * @since 2022/1/7
 */
public abstract class AbstractRandomTypeAutoGenerator<T> implements TestDataAutoGenerator {

    protected static final String NULL = "null";
    private static SecureRandom secureRandom = new SecureRandom();

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            secureRandom = new SecureRandom();
        }
    }

    /**
     * 空值概率
     */
    private final double nullProbability;
    /**
     * 随机值下界字面值
     */
    protected String lowerBoundLiteral;
    /**
     * 随机值上界字面值
     */
    protected String upperBoundLiteral;
    /**
     * 随机值下界
     */
    protected T lowerBound;
    /**
     * 随机值上界
     */
    protected T upperBound;

    /**
     * 创建随机值生成器
     *
     * @param generationRuleExpression 生成规则表达式
     */
    protected AbstractRandomTypeAutoGenerator(String generationRuleExpression) {
        // 按 ";" 分割所有枚举选项的生成规则
        String[] generationRules = generationRuleExpression.split(";");

        // 获取区间下上界字面值: lowerBound, upperBound
        String interval = generationRules[0].split("\\|")[0];
        this.lowerBoundLiteral = interval.split(",")[0];
        this.upperBoundLiteral = interval.split(",")[1];

        // 添加上下界空值校验
        if (StringUtils.isEmpty(this.lowerBoundLiteral) || NULL.equals(this.lowerBoundLiteral)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "范围最小值不能为空，请重新设置并保存生成规则。");
        }
        if (StringUtils.isEmpty(this.upperBoundLiteral) || NULL.equals(this.upperBoundLiteral)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "范围最大值不能为空，请重新设置并保存生成规则。");
        }

        // 空值概率: nul|nullProbability
        this.nullProbability = Double.parseDouble(generationRules[1].split("\\|")[1]);
    }

    @Override
    public String getValue() {
        double randomDouble = secureRandom.nextDouble();

        if (randomDouble < nullProbability) {
            // 空值
            return "";
        } else {
            return getConcreteValue(lowerBound, upperBound);
        }
    }

    /**
     * 获取具体的随机值（非空）
     *
     * @param lowerBound 随机值下界
     * @param upperBound 随机值上界
     * @return 非空随机值
     */
    public abstract String getConcreteValue(T lowerBound, T upperBound);
}
