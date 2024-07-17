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
import com.wiseco.var.process.app.server.controller.vo.PercentageMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.service.statistics.AlgorithmService;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import com.wisecotech.json.JSON;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.statistics.SpecialRatioIndexCalculateStrategy.getSpecialNumberValuesList;

/**
 * 计算分位数
 */
@Component
public class PercentageIndexCalculateStrategy implements IndexCalculateStrategy {

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

        PercentageMappingVo percentageMappingVo = new PercentageMappingVo();

        if (lastValues.isEmpty()) {
            percentageMappingVo.setPercentage1(null);
            percentageMappingVo.setPercentage5(null);
            percentageMappingVo.setPercentage25(null);
            percentageMappingVo.setPercentage50(null);
            percentageMappingVo.setPercentage75(null);
            percentageMappingVo.setPercentage95(null);
            resultVo.setPercentageResult(JSON.toJSONString(percentageMappingVo));
        } else {
            percentageMappingVo.setPercentage1(BigDecimal.valueOf(AlgorithmService.calculatePercentile(lastValues, MagicNumbers.DOUBLE_0_01)).setScale(MagicNumbers.TWO, RoundingMode.HALF_UP));
            percentageMappingVo.setPercentage5(BigDecimal.valueOf(AlgorithmService.calculatePercentile(lastValues, MagicNumbers.DOUBLE_0_05)).setScale(MagicNumbers.TWO, RoundingMode.HALF_UP));
            percentageMappingVo.setPercentage25(BigDecimal.valueOf(AlgorithmService.calculatePercentile(lastValues, MagicNumbers.DOUBLE_0_25)).setScale(MagicNumbers.TWO, RoundingMode.HALF_UP));
            percentageMappingVo.setPercentage50(BigDecimal.valueOf(AlgorithmService.calculatePercentile(lastValues, MagicNumbers.DOUBLE_0_50)).setScale(MagicNumbers.TWO, RoundingMode.HALF_UP));
            percentageMappingVo.setPercentage75(BigDecimal.valueOf(AlgorithmService.calculatePercentile(lastValues, MagicNumbers.DOUBLE_0_75)).setScale(MagicNumbers.TWO, RoundingMode.HALF_UP));
            percentageMappingVo.setPercentage95(BigDecimal.valueOf(AlgorithmService.calculatePercentile(lastValues, MagicNumbers.DOUBLE_0_95)).setScale(MagicNumbers.TWO, RoundingMode.HALF_UP));
            resultVo.setPercentageResult(JSON.toJSONString(percentageMappingVo));
        }
    }

    @Override
    public String getIndexName() {
        return AnalysisIndexEnum.PERCENTAGE.getCode();
    }
}
