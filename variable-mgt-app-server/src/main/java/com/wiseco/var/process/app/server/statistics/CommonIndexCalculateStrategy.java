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
import com.wiseco.var.process.app.server.commons.enums.AnalysisIndexEnum;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.statistics.SpecialRatioIndexCalculateStrategy.getSpecialNumberValuesList;

/**
 * 公共计算 最小最大 均值
 */
@Component
public class CommonIndexCalculateStrategy implements IndexCalculateStrategy {

    /**
     * 计算统计分析结果
     *
     * @param resultVo           入参
     * @param calculateContext 上下文
     */
    @Override
    public void calculateVar(StatisticsResultVo resultVo, IndexCalculateContext calculateContext) {
        String fieldType = resultVo.getDataType();
        Assert.notNull(fieldType, "fieldType must not be null");
        boolean isNumber = fieldType.equals(VarDataTypeEnum.INTEGER.getDesc())
                || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())
                || fieldType.equals(CommonConstant.FLOAT_STR)
                || fieldType.equals(CommonConstant.LONG_STR);
        if (!isNumber) {
            return;
        }
        // 获取缺失值
        final String defaultValue = getDefaultValue(calculateContext.getDataTypeConfigMap(), fieldType);
        // 获取特殊值
        final List<Double> specialValues = getSpecialNumberValuesList(resultVo,calculateContext, fieldType);
        // 获取字段数据
        final List<Double> numberValues = getDoubleValues(calculateContext.getDataList(), resultVo.getVarCode(), defaultValue);
        // 去掉特殊值和缺失值
        final List<Double> lastValues = numberValues.stream()
                .filter(num -> !NumberUtils.createDouble(defaultValue).equals(num) && !specialValues.contains(num))
                .collect(Collectors.toList());
        final OptionalDouble max = lastValues.stream().mapToDouble(Double::doubleValue).max();
        final OptionalDouble min = lastValues.stream().mapToDouble(Double::doubleValue).min();
        final double avg = lastValues.stream().mapToDouble(Double::doubleValue).average().orElse(0D);
        resultVo.setMaxVal(max.isPresent() ? BigDecimal.valueOf(max.getAsDouble()) : BigDecimal.ZERO);
        resultVo.setMinimumVal(min.isPresent() ? BigDecimal.valueOf(min.getAsDouble()) : BigDecimal.ZERO);
        resultVo.setAverageVal(BigDecimal.valueOf(avg));
    }

    @Override
    public String getIndexName() {
        return AnalysisIndexEnum.COMMON.getCode();
    }

}
