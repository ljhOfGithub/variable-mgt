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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 随机日期时间 测试数据在线自动生成器
 *
 * @author Zhaoxiong Chen
 * @since 2022/1/10
 */
public class AbstractRandomDateTimeAutoGenerator extends AbstractRandomTypeAutoGenerator<LocalDateTime> {

    /**
     * 日期时间格式
     */
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * AbstractRandomDateTimeAutoGenerator
     *
     * @param generationRuleExpression 生成规则表达式
     */
    public AbstractRandomDateTimeAutoGenerator(String generationRuleExpression) {
        super(generationRuleExpression);

        String[] startSplit = this.lowerBoundLiteral.replace(" ", "-").replace(":", "-").split("-");
        String[] endSplit = this.upperBoundLiteral.replace(" ", "-").replace(":", "-").split("-");

        this.lowerBound = LocalDateTime.of(Integer.parseInt(startSplit[0]), Integer.parseInt(startSplit[1]), Integer.parseInt(startSplit[MagicNumbers.TWO]),
                Integer.parseInt(startSplit[MagicNumbers.THREE]), Integer.parseInt(startSplit[MagicNumbers.FOUR]), Integer.parseInt(startSplit[MagicNumbers.FIVE]));

        this.upperBound = LocalDateTime.of(Integer.parseInt(endSplit[0]), Integer.parseInt(endSplit[1]), Integer.parseInt(endSplit[MagicNumbers.TWO]),
                Integer.parseInt(endSplit[MagicNumbers.THREE]), Integer.parseInt(endSplit[MagicNumbers.FOUR]), Integer.parseInt(endSplit[MagicNumbers.FIVE]));

    }

    @Override
    public synchronized String getConcreteValue(LocalDateTime lowerBound, LocalDateTime upperBound) {
        long time = DateTimeSource.getInstance().randomTimestamp(lowerBound, upperBound);
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }
}
