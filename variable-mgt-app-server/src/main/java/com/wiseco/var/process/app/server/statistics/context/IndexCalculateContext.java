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
package com.wiseco.var.process.app.server.statistics.context;

import com.wiseco.var.process.app.server.controller.vo.AnalysisIndexMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigSpecialMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "统计分析计算")
public class IndexCalculateContext {
    /**
     * context来源 1：统计分析  2：监控单指标分析
     */
    private Integer contextType = 1;

    Long manifestId;
    /**
     * 表数据
     */
    List<Map<String, Object>> dataList;
    /**
     * 算法列表
     */
    List<String> calculateList;
    Map<String, VarProcessConfigDefault> dataTypeConfigMap;
    /**
     * 清单中的全部变量和类型
     */
    List<Map<String, Object>> allManifestVarsType;
    /**
     * 分列计算，当前计算列的指标和类型
     */
    List<Map<String, Object>> currentVarsType;
    /**
     * 服务id
     */
    Long varProcessServiceId;
    /**
     * 结果
     */
    List<StatisticsResultVo> resultList;
    /**
     * 分析指标
     */
    AnalysisIndexMappingVo indexMappingVo;
    /**
     * iv值计算参数设置
     */
    ConfigIvMappingVo ivMappingVo;
    /**
     * psi计算参数配置
     */
    ConfigPsiMappingVo psiMappingVo;
    /**
     * 特殊值参数配置
     */
    List<ConfigSpecialMappingVo> specialMappingVoList;
    /**
     * 批量回溯任务批次号
     */
    String batchNumber;
    String target;
    /**
     * 获取表的id，统计分析为manifestId，批量回溯为backtrackingId
     */
    Long tableSourceId;
    Long statisticsConfigId;
    Long backtrackingConfigId;
    /**
     * 指标名称name:label
     */
    Map<String, String> varNameMap;
    /**
     * 开始时间
     */
    private LocalDateTime startDate;
    /**
     * 结束时间
     */
    private LocalDateTime endDate;
    /**
     * 要计算的变量code，为空则计算全部
     */
    private List<String> calculateVariableList;
    /**
     * 数据集信息
     */
    private List<DataSourceInfo> dataSourceInfoList;
    /**
     * 清单中的全部变量和类型 key：清单id value类型
     */
    private Map<Long, List<Map<String, Object>>> allManifestVarsTypeListMap;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class DataSourceInfo {
        private Long serviceId;
        private Long manifestId;
    }
}
