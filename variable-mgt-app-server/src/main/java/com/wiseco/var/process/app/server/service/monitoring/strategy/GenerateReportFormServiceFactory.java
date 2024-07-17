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
package com.wiseco.var.process.app.server.service.monitoring.strategy;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.ReportFormCategoryEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuweikang
 */
@Component
public class GenerateReportFormServiceFactory {

    @Autowired
    private final Map<String, GenerateReportFormStrategy> serviceMap = new HashMap<>(MagicNumbers.EIGHT);

    /**
     * 获取生成报表服务
     * @param reportFormCategoryEnum 报表分类枚举
     * @return com.wiseco.var.process.app.server.service.monitoring.strategy.GenerateReportFormServiceFactory
     */
    public GenerateReportFormStrategy getGenerateReportFormService(ReportFormCategoryEnum reportFormCategoryEnum) {
        GenerateReportFormStrategy generateReportFormStrategy;
        if (reportFormCategoryEnum.equals(ReportFormCategoryEnum.SERVICE) || reportFormCategoryEnum.equals(ReportFormCategoryEnum.SINGLE_VARIABLE_ANALYZE) || reportFormCategoryEnum.equals(ReportFormCategoryEnum.VARIABLE_COMPARE_ANALYZE)) {
            generateReportFormStrategy = serviceMap.get(reportFormCategoryEnum.getName());
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "未知的报表类型");
        }
        return generateReportFormStrategy;
    }

}
