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
package com.wiseco.var.process.app.server.controller.vo;

import com.wiseco.var.process.app.server.enums.StatisticalOverallAnalyzeDataItemsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "统计分析 iv参数配置vo")
public class ConfigIvMappingVo implements Serializable {
    private static final long SERIAL_VERSION_UID = 8759865846955173993L;

    /**
     * 来源 0实时服务 1内部数据表
     */
    @Schema(description = "来源 0实时服务或变量回溯 1内部数据表")
    private Integer sourceType;

    @Schema(description = "y指标")
    private String targetField;

    @Schema(description = "good标签值")
    private List<String> goodValues;

    @Schema(description = "bad标签值")
    private List<String> badValues;

    @Schema(description = "数据表")
    private String tableName;

    @Schema(description = "表关联字段")
    private String relationField;

    @Schema(description = "分组分析字段")
    private String groupedField;

    @Schema(description = "分析数据项, FIRST——first, LAST——last, APPOINT——appoint")
    private StatisticalOverallAnalyzeDataItemsEnum groupedFieldValueOption;

    @Schema(description = "指定数据项")
    private List<String> groupedFieldValue;

}
