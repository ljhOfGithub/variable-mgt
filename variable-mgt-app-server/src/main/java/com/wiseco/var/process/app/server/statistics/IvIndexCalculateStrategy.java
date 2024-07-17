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

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.AnalysisIndexEnum;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.service.statistics.AlgorithmService;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class IvIndexCalculateStrategy implements IndexCalculateStrategy {

    @Override
    public void calculateVar(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        if (!valid(result, calculateContext)) {
            return;
        }
        String fieldType = result.getDataType();
        ConfigIvMappingVo ivMappingVo = calculateContext.getIvMappingVo();
        String varCode = result.getVarCode();
        List<Map<String, Object>> dataList = calculateContext.getDataList();
        calculateContext.setTarget(ivMappingVo.getTargetField());

        final String defaultValue = getDefaultValue(calculateContext.getDataTypeConfigMap(), fieldType);

        // 换算Y的好坏为0和1，如果有没选中的值，需要移除
        final List<String> yVarValues = getStringValues(dataList, CommonConstant.TARGET_NAME, defaultValue);
        Assert.notEmpty(yVarValues, "Y的值不能为空");
        List<Integer> targetList = new ArrayList<>();
        final List<String> goodValues = ivMappingVo.getGoodValues();
        final List<String> badValues = ivMappingVo.getBadValues();
        final List<Integer> needRemoveIndex = new ArrayList<>();
        for (int i = 0; i < yVarValues.size(); i++) {
            final String y = yVarValues.get(i);
            if (goodValues.contains(y)) {
                targetList.add(0);
            } else if (badValues.contains(y)) {
                targetList.add(1);
            } else {
                needRemoveIndex.add(i);
            }
        }

        Double iv = null;
        if (fieldType.equals(VarDataTypeEnum.STRING.getDesc())) {
            final List<String> varValues = getStringValues(dataList, varCode, defaultValue);
            for (int i = needRemoveIndex.size() - 1; i >= 0; i--) {
                varValues.remove(needRemoveIndex.get(i).intValue());
            }
            iv = AlgorithmService.calculateStringIv(targetList, varValues);
        } else {
            final List<Double> numberValues = getDoubleValues(dataList, varCode, defaultValue);
            for (int i = needRemoveIndex.size() - 1; i >= 0; i--) {
                numberValues.remove(needRemoveIndex.get(i).intValue());
            }
            iv = AlgorithmService.calculateDoubleIv(targetList, numberValues);
        }
        if (iv != null) {
            result.setIvResult(BigDecimal.valueOf(iv).setScale(MagicNumbers.THREE, RoundingMode.HALF_UP));
        }
    }

    private boolean valid(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        String fieldType = result.getDataType();
        //日期类型不计算iv值
        if (fieldType == null || fieldType.equals(VarDataTypeEnum.DATE.getDesc())
                || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())
                || fieldType.equals(VarDataTypeEnum.BOOLEAN.getDesc())) {
            return false;
        }
        ConfigIvMappingVo ivMappingVo = calculateContext.getIvMappingVo();
        if (ivMappingVo == null || CollUtil.isEmpty(ivMappingVo.getGoodValues()) || CollUtil.isEmpty(ivMappingVo.getBadValues()) || StringUtils.isEmpty(ivMappingVo.getTargetField())) {
            return false;
        }

        String indexCode = result.getVarCode();
        String yIndex = ivMappingVo.getTargetField();

        //当前列不计算
        return !indexCode.equals(yIndex);
    }

    @Override
    public String getIndexName() {
        return AnalysisIndexEnum.IV.getCode();
    }
}
