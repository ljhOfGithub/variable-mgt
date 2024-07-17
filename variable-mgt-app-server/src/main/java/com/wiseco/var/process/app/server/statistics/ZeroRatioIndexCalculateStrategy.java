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
package com.wiseco.var.process.app.server.statistics;

import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.AnalysisIndexEnum;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 零值占比计算
 */
@Component
public class ZeroRatioIndexCalculateStrategy implements IndexCalculateStrategy {

    @Override
    public void calculateVar(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        String fieldType = result.getDataType();
        Assert.notNull(fieldType, "fieldType must not be null");
        boolean isNumber = fieldType.equals(VarDataTypeEnum.INTEGER.getDesc())
                || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())
                || fieldType.equals(CommonConstant.FLOAT_STR)
                || fieldType.equals(CommonConstant.LONG_STR);
        if (!isNumber) {
            return;
        }

        List<Map<String, Object>> dataList = calculateContext.getDataList();
        List<Object> valList = dataList.stream().filter(Objects::nonNull).map(e -> e.get(result.getVarCode())).collect(Collectors.toList());

        long zeroCount;
        if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc())) {
            zeroCount = valList.stream().filter(e -> e != null && (Integer) e == 0L).count();
        } else if (fieldType.equals(CommonConstant.LONG_STR)) {
            zeroCount = valList.stream().filter(e -> e != null && (Long) e == 0L).count();
        } else {
            zeroCount = valList.stream().filter(e -> e != null && (Double) e == 0d).count();
        }

        //保留两位小数
        result.setZeroRatio(new BigDecimal(zeroCount).divide(new BigDecimal(dataList.size()), MagicNumbers.FOUR, RoundingMode.HALF_UP));
    }

    @Override
    public String getIndexName() {
        return AnalysisIndexEnum.ZERO_RATIO.getCode();
    }
}
