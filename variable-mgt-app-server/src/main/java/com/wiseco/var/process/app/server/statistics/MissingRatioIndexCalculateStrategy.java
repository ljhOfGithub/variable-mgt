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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.AnalysisIndexEnum;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 缺失值占比计算
 */
@Component
@Slf4j
public class MissingRatioIndexCalculateStrategy implements IndexCalculateStrategy {

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String ONE = "1";
    private static final String ZERO = "0";
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void calculateVar(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        String fieldType = result.getDataType();
        if (!(fieldType == null || fieldType.equals(CommonConstant.LONG_STR))) {
            Map<String, VarProcessConfigDefault> dataTypeConfigMap = calculateContext.getDataTypeConfigMap();
            String indexCode = result.getVarCode();

            List<Map<String, Object>> dataList = calculateContext.getDataList();
            List<Object> valList = dataList.stream().map(e -> e.get(indexCode)).collect(Collectors.toList());

            VarProcessConfigDefault processConfigDefault = dataTypeConfigMap.get(fieldType);
            if (processConfigDefault == null || StringUtils.isEmpty(processConfigDefault.getDefaultValue())) {
                return;
            }
            long missingCount = 0L;

            if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc())) {
                missingCount = valList.stream().filter(e -> e != null && e.equals(Integer.parseInt(processConfigDefault.getDefaultValue()))).count();
            } else if (fieldType.equals(VarDataTypeEnum.STRING.getDesc())) {
                missingCount = valList.stream().filter(e -> e != null && e.equals(processConfigDefault.getDefaultValue())).count();
            } else if (fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                missingCount = valList.stream().filter(e -> e != null && e.equals(Double.parseDouble(processConfigDefault.getDefaultValue()))).count();
            } else if (fieldType.equals(VarDataTypeEnum.DATE.getDesc())) {
                missingCount = valList.stream().map(e -> {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    return formatter.format((Date) e);
                }).filter(dateStr -> dateStr.equals(processConfigDefault.getDefaultValue())).count();
            } else if (fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                missingCount = valList.stream().map(e -> datetimeFormat.format((Date) e)).filter(dateStr -> dateStr.equals(processConfigDefault.getDefaultValue())).count();
            } else if (fieldType.equals(VarDataTypeEnum.BOOLEAN.getDesc())) {
                Integer specialVal = null;
                if (processConfigDefault.getDefaultValue().equals(ZERO) || processConfigDefault.getDefaultValue().equals(FALSE)) {
                    specialVal = 0;
                }
                if (processConfigDefault.getDefaultValue().equals(ONE) || processConfigDefault.getDefaultValue().equals(TRUE)) {
                    specialVal = 1;
                }
                Integer finalSpecialVal = specialVal;
                missingCount = valList.stream().filter(e -> e != null && e.equals(finalSpecialVal)).count();
            }

            //保留两位小数
            result.setMissingRatio(new BigDecimal(missingCount).divide(new BigDecimal(dataList.size()), MagicNumbers.FOUR, RoundingMode.HALF_UP));
        }
    }

    @Override
    public String getIndexName() {
        return AnalysisIndexEnum.MISSING_RATIO.getCode();
    }
}
