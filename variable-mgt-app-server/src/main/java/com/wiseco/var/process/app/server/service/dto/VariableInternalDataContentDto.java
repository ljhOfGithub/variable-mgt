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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariableInternalDataContentDto implements Serializable {

    @Schema(description = "空间ID", example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long spaceId;

    @Schema(description = "内部数据ID", example = "1")
    @NotNull(message = "内部数据ID不能为空")
    private Long dataId;

    @Schema(description = "是否保存数据模型:0-否，1-是", example = "1")
    @NotBlank(message = "保存至数据模型不能为空")
    private String isSaveDataModel;

    @Schema(description = "内部数据名称", example = "")
    @NotBlank(message = "内部数据名称不能为空")
    private String name;

    @Schema(description = "对象名称", example = "")
    @NotBlank(message = "对象名称不能为空")
    private String objectName;

    @Schema(description = "对象中文名", example = "")
    @NotBlank(message = "对象中文名不能为空")
    private String objectLabel;

    @Schema(description = "对象中文名", example = "null")
    private ContentDto content;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "内部数据配置")
    public static class ContentDto implements Serializable {

        @Schema(description = "入参信息", example = "null")
        private List<InputDto> input;

        @Schema(description = "返回数据", example = "null")
        private List<OutputDto> output;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "入参信息")
    public static class InputDto implements Serializable {

        @Schema(description = "参数名", example = "")
        private String name;

        @Schema(description = "参数中文名", example = "")
        private String label;

        @Schema(description = "数据类型", example = "")
        private String dataType;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "返回数据")
    public static class OutputDto implements Serializable {

        @Schema(description = "对象名称", example = "")
        private String objectName;

        @Schema(description = "对象描述", example = "")
        private String objectLabel;

        @Schema(description = "是否数组", example = "")
        private String isArr;

        @Schema(description = "是否映射表", example = "")
        private String isMapping;

        @Schema(description = "表字段配置", example = "null")
        private TableConfigsDto tableConfigs;

        @Schema(description = "子项", example = "null")
        private List<OutputDto> children;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "表字段配置")
    public static class TableConfigsDto implements Serializable {

        @Schema(description = "表名", example = "")
        private String tableName;

        @Schema(description = "条件", example = "")
        private String conditions;

        @Schema(description = "字段映射列表", example = "null")
        private List<FieldMappingDto> fieldMapping;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "字段映射")
    public static class FieldMappingDto implements Serializable {

        @Schema(description = "字段名", example = "null")
        private String name;

        @Schema(description = "字段描述", example = "null")
        private String label;

        @Schema(description = "字段类型", example = "null")
        private String columnType;

        @Schema(description = "数据类型", example = "null")
        private String dataType;

        @Schema(description = "字段长度", example = "null")
        private Integer dataSize;

        @Schema(description = "小数位长度", example = "null")
        private Integer digits;

        @Schema(description = "数据名称", example = "null")
        private String mappingName;

        @Schema(description = "数据描述", example = "null")
        private String mappingLabel;

        @Schema(description = "数据类型", example = "null")
        private String mappingDataType;

    }

}
