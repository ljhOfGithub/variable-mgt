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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;

/**
 * 随机双精度浮点数 测试数据在线自动生成器
 *
 * <p>
 * 生成规则示例:
 * lowerBoundLiteral,upperBoundLiteral|1-nullProbability;nul|nullProbability
 * </p>
 *
 * @author Zhaoxiong Chen
 */
public class AbstractRandomDoubleAutoGenerator extends AbstractRandomTypeAutoGenerator<Double> {

    private static SecureRandom secureRandom = new SecureRandom();

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            secureRandom = new SecureRandom();
        }
    }

    /**
     * 浮点数格式
     */
    private final DecimalFormat decimalFormat;

    /**
     * AbstractRandomDoubleAutoGenerator
     *
     * @param generationRuleExpression 生成规则表达式
     */
    public AbstractRandomDoubleAutoGenerator(String generationRuleExpression) {
        super(generationRuleExpression);

        this.lowerBound = Double.parseDouble(lowerBoundLiteral);
        this.upperBound = Double.parseDouble(upperBoundLiteral);

        // 设置浮点数格式 0.00
        this.decimalFormat = new DecimalFormat("0.00");
    }

    @Override
    public String getConcreteValue(Double lowerBound, Double upperBound) {
        // 随机 double 区间: [lowerBound, upperBound)
        double random = lowerBound + secureRandom.nextDouble() * (upperBound - lowerBound);

        return decimalFormat.format(random);
    }
}
