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

import org.apache.commons.lang3.Validate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 随机整数 测试数据在线自动生成器
 *
 * @author Zhaoxiong Chen
 * @since 2022/1/10
 */
public class AbstractRandomIntegerAutoGenerator extends AbstractRandomTypeAutoGenerator<Integer> {

    private static SecureRandom secureRandom = new SecureRandom();

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            secureRandom = new SecureRandom();
        }
    }

    /**
     * AbstractRandomIntegerAutoGenerator
     *
     * @param generationRuleExpression 生成规则表达式
     */
    public AbstractRandomIntegerAutoGenerator(String generationRuleExpression) {
        super(generationRuleExpression);

        this.lowerBound = Integer.parseInt(lowerBoundLiteral);
        this.upperBound = Integer.parseInt(upperBoundLiteral);
    }

    @Override
    public String getConcreteValue(Integer lowerBound, Integer upperBound) {
        long random = nextInt(lowerBound, upperBound + 1);

        return String.valueOf(random);
    }

    private int nextInt(int startInclusive, int endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.");
        return startInclusive == endExclusive ? startInclusive : startInclusive + secureRandom.nextInt(endExclusive - startInclusive);
    }
}
