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

import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.test.TestDataAutoGenerator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 枚举 测试数据在线自动生成器
 * <p>
 * 生成规则示例:
 * A选项|0.1;B选项|0.1;C选项|0.8
 * 空值用nul
 *
 * @author Zhaoxiong Chen
 * @since 2022/1/7
 */
public class EnumTypeAutoGenerator implements TestDataAutoGenerator {

    private static SecureRandom secureRandom = new SecureRandom();

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            secureRandom = new SecureRandom();
        }
    }

    /**
     * 枚举项数量
     */
    private final int enumSize;
    /**
     * 枚举项内容
     */
    private final String[] enumItems;
    /**
     * 枚举项对应的概率最大值 "概率轴"
     */
    private final double[] probabilityAxis;

    /**
     * EnumTypeAutoGenerator
     *
     * @param generationRuleExpression 生成规则表达式
     */
    public EnumTypeAutoGenerator(String generationRuleExpression) {
        // 按 ";" 分割所有枚举选项的生成规则
        String[] generateRules = generationRuleExpression.split(";");

        this.enumSize = generateRules.length;
        this.enumItems = new String[this.enumSize];
        this.probabilityAxis = new double[this.enumSize];

        for (int i = 0; i < generateRules.length; i++) {
            // 按 "|" 分割每一个枚举选项的内容和概率
            String[] ruleItem = generateRules[i].split("\\|");

            // 填充枚举项数据 处理空值
            enumItems[i] = ruleItem[0].equals(CommonConstant.DEFAULT_TEST_NULL) ? "" : ruleItem[0];

            // 填充枚举项概率轴节点
            double probability = Double.parseDouble(ruleItem[1]);
            probabilityAxis[i] = i > 0 ? probability + probabilityAxis[i - 1] : probability;
        }
    }

    @Override
    public String getValue() {
        double randomDouble = secureRandom.nextDouble();

        // 比较所有枚举项的概率
        for (int i = 0; i < enumSize; i++) {
            if (randomDouble < probabilityAxis[i]) {
                return enumItems[i];
            }
        }

        return "";
    }
}
