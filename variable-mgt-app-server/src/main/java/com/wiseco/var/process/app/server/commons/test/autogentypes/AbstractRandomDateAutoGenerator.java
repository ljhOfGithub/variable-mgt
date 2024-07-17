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

import com.apifan.common.random.source.DateTimeSource;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;

import java.time.LocalDate;

/**
 * 随机日期 测试数据在线自动生成器
 *
 * @author Zhaoxiong Chen
 * @since 2022/1/10
 */
public class AbstractRandomDateAutoGenerator extends AbstractRandomTypeAutoGenerator<LocalDate> {

    /**
     * AbstractRandomDateAutoGenerator
     *
     * @param generationRuleExpression 生成规则表达式
     */
    public AbstractRandomDateAutoGenerator(String generationRuleExpression) {
        super(generationRuleExpression);

        String[] startDateSplit = this.lowerBoundLiteral.split("-");
        String[] endDateSplit = this.upperBoundLiteral.split("-");
        this.lowerBound = LocalDate.of(Integer.parseInt(startDateSplit[0]), Integer.parseInt(startDateSplit[1]), Integer.parseInt(startDateSplit[MagicNumbers.TWO]));
        this.upperBound = LocalDate.of(Integer.parseInt(endDateSplit[0]), Integer.parseInt(endDateSplit[1]), Integer.parseInt(endDateSplit[MagicNumbers.TWO]));
    }

    @Override
    public String getConcreteValue(LocalDate lowerBound, LocalDate upperBound) {
        return DateTimeSource.getInstance().randomDate(lowerBound, upperBound, "yyyy-MM-dd");
    }
}
