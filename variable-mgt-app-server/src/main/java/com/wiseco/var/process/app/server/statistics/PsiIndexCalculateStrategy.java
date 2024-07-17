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


import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.AnalysisIndexEnum;
import com.wiseco.var.process.app.server.controller.vo.ConfigPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.service.statistics.AlgorithmService;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import com.wiseco.var.process.app.server.statistics.template.OverallBacktrackingStatistics;
import com.wiseco.var.process.app.server.statistics.template.OverallProcessStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PsiIndexCalculateStrategy implements IndexCalculateStrategy {
    @Resource
    private OverallBacktrackingStatistics overallBacktrackingStatistics;

    @Resource
    OverallProcessStatistics overallProcessStatistics;

    @Override
    public void calculateVar(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        String fieldType = result.getDataType();
        //日期类型不计算psi值
        if (!(fieldType == null || fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc()) || fieldType.equals(VarDataTypeEnum.BOOLEAN.getDesc()))) {
            ConfigPsiMappingVo psiMappingVo = calculateContext.getPsiMappingVo();
            boolean flag = psiMappingVo == null || (!psiMappingVo.getBaseIndexFlag() && (psiMappingVo.getBaseIndex() == null || psiMappingVo.getBaseIndexVal() == null) && (psiMappingVo.getBaseServiceId() == null && psiMappingVo.getBaseManifestId() == null));
            if (flag) {
                return;
            }

            Map<String, List<Object>> psiDataMap;
            if (calculateContext.getStartDate() != null || calculateContext.getEndDate() != null || calculateContext.getContextType() == MagicNumbers.TWO) {
                psiDataMap = overallProcessStatistics.getPsiDataMap(result, calculateContext);
            } else {
                psiDataMap = overallBacktrackingStatistics.getPsiDataMap(result, calculateContext);
            }

            // data1 基准数列, data2 目标数列
            Double psiValue = null;
            if (psiDataMap.containsValue(null)) {
                log.info("整体分析计算psi参数信息：指标code:{}，指标类型:{}，获取待计算数据为空，不进行计算", result.getVarCode(), fieldType);
            } else if (VarDataTypeEnum.INTEGER.getDesc().equals(fieldType) || VarDataTypeEnum.DOUBLE.getDesc().equals(fieldType)) {
                List<Double> data1 = psiDataMap.get("data1").stream().filter(Objects::nonNull).filter(item -> !"null".equals(item.toString())).map(item -> Double.valueOf(item.toString())).collect(Collectors.toList());
                List<Double> data2 = psiDataMap.get("data2").stream().filter(Objects::nonNull).filter(item -> !"null".equals(item.toString())).map(item -> Double.valueOf(item.toString())).collect(Collectors.toList());
                psiValue = AlgorithmService.calculateDoublePsi(data1, data2);
            } else if (VarDataTypeEnum.STRING.getDesc().equals(fieldType)) {
                List<String> data1 = psiDataMap.get("data1").stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
                List<String> data2 = psiDataMap.get("data2").stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
                psiValue = AlgorithmService.calculateStringPsi(data1, data2);
            }

            if (psiValue != null) {
                result.setPsiResult(BigDecimal.valueOf(psiValue).setScale(MagicNumbers.THREE, RoundingMode.HALF_UP));
            }
        }
    }

    @Override
    public String getIndexName() {
        return AnalysisIndexEnum.PSI.getCode();
    }
}
