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

import com.wiseco.var.process.app.server.commons.enums.RightValueTypeEnum;
import com.wiseco.var.process.app.server.enums.FilterRuleEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessCalFunctionEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessFilterConditionCmpEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessPeriodEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessTemplateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "流式变量content")
@ToString
public class StreamProcessContentInputVO {

    @Schema(description = "场景id")
    private Long sceneId;

    @Schema(description = "变量加工模板")
    private StreamProcessTemplateEnum processTemplate;

    @Schema(description = "计算函数")
    private StreamProcessCalFunctionEnum calculateFunction;

    @Schema(description = "时间id")
    private Long eventId;

    @Schema(description = "匹配维度")
    private String matchDimension;

    @Schema(description = "统计变量")
    private String calculateVar;

    @Schema(description = "统计周期")
    private StreamProcessContentInputVO.CalculatePeriod calculatePeriod;

    @Schema(description = "是否包含本笔")
    private Boolean currentIncluded;

    @Schema(description = "过滤条件")
    private StreamProcessContentInputVO.FilterCondition filterConditionInfo;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "统计周期")
    @ToString
    public static class CalculatePeriod implements Serializable {

        @Schema(description = "描述符",allowableValues = {"RECENT","PREVIOUS","CURRENT"})
        private StreamProcessPeriodEnum periodDescriptor;

        @Schema(description = "周期时间")
        private Integer periodTime;

        @Schema(description = "时间单位",allowableValues = {"SECOND","MINUTE","HOUR","DAY"})
        private StreamProcessPeriodEnum.TimeUnitEnum periodUnit;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "过滤条件")
    @ToString
    public static class FilterCondition implements Serializable {
        @Schema(description = "过滤规则",allowableValues = {"AND","OR","NONE",})
        private FilterRuleEnum filterRule;

        @Schema(description = "过滤条件")
        private List<StreamProcessContentInputVO.FilterConditionDetail> conditionList;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "过滤条件详情")
    @ToString
    public static class FilterConditionDetail implements Serializable {

        @Schema(description = "数据模型字段 path")
        @NotEmpty(message = "过滤条件填写不完全，请检查")
        private String dataModelVar;

        @Schema(description = "数据模型字段Label：英文名_中文名")
        private String dataModelLabel;

        @Schema(description = "左值类型")
        private String type;

        @NotNull(message = "过滤条件填写不完全，请检查")
        @Schema(description = "比较符",allowableValues = {"IS_NULL","IS_NOT_NULL","EQUAL","NOT_EQUAL","GREATER_THAN","GREATER_THAN_OR_EQUAL","LESS_THAN","LESS_THAN_OR_EQUAL","CONTAIN","NOT_CONTAIN","START_WITH","END_WITH"})
        private StreamProcessFilterConditionCmpEnum comparisonOperators;

        @NotNull(message = "过滤条件填写不完全，请检查")
        @Schema(description = "右值类型",allowableValues = {"VALUE_INPUT","VAR_SELECT"})
        private RightValueTypeEnum rightValueType;

        @Schema(description = "右值Label：英文名_中文名")
        private String rightValueLabel;

        @NotEmpty(message = "过滤条件填写不完全，请检查")
        @Schema(description = "右值字段 path/手动输入")
        private String rightValue;
    }
}
