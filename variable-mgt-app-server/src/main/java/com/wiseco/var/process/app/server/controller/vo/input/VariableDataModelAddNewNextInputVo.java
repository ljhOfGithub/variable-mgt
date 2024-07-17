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
package com.wiseco.var.process.app.server.controller.vo.input;

import com.wiseco.var.process.app.server.enums.VarProcessDataModeInsideDataType;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "添加数据模型 DTO")
public class VariableDataModelAddNewNextInputVo implements Serializable {

    @Schema(description = "数据模型ID")
    private Long dataModelId;

    @Schema(description = "改变标志")
    private Boolean isChange;

    @Schema(description = "对象名称")
    @NotBlank(message = "对象名称不能为空")
    private String objectName;

    @Schema(description = "对象中文名")
    @NotBlank(message = "对象中文名不能为空")
    private String objectLabel;

    @Schema(description = "对象来源")
    private VarProcessDataModelSourceType sourceType;

    @Schema(description = "内部数据", example = "null")
    private DataModelInsideDataVO insideData;

    @Schema(description = "外部数据服务", example = "null")
    private DataModelOutsideServerVO outsideServer;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "内部数据配置")
    public static class DataModelInsideDataVO implements Serializable {

        @Schema(description = "对象获取参数信息", example = "null")
        private List<InsideInputVO> input;

        @Schema(description = "对象获取内容-获取方式")
        @NotBlank(message = "对象获取内容-获取方式")
        private VarProcessDataModeInsideDataType insideDataType;

        @Schema(description = "对象获取内容-表及字段映射", example = "null")
        private List<InsideOutputVO> tableOutput;

        @Schema(description = "sql引用内部数据表", example = "null")
        private List<String> sqlTableNames;

        @Schema(description = "取数逻辑（sql）")
        private String sqlString;

        @Schema(description = "是否返回多条")
        private Boolean sqlIsArray;

        @Schema(description = "sql返回数据定义", example = "null")
        private List<InsideSqlOutputVO> sqlOutput;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "入参信息")
    public static class InsideInputVO implements Serializable {

        @Schema(description = "参数名")
        private String name;

        @Schema(description = "参数中文名")
        private String label;

        @Schema(description = "数据类型")
        private String dataType;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "返回数据")
    public static class InsideOutputVO implements Serializable {

        @Schema(description = "对象名称")
        private String objectName;

        @Schema(description = "对象描述")
        private String objectLabel;

        @Schema(description = "是否数组")
        private String isArr;

        @Schema(description = "是否映射表")
        private String isMapping;

        @Schema(description = "表字段配置", example = "null")
        private TableConfigsVO tableConfigs;

        @Schema(description = "子项", example = "null")
        private List<InsideOutputVO> children;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "表字段配置")
    public static class TableConfigsVO implements Serializable {

        @Schema(description = "表名")
        private String tableName;

        @Schema(description = "条件")
        private String conditions;

        @Schema(description = "字段映射列表", example = "null")
        private List<FieldMappingVO> fieldMapping;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "字段映射")
    public static class FieldMappingVO implements Serializable {

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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Sql返回数据")
    public static class InsideSqlOutputVO implements Serializable {

        @Schema(description = "名称")
        private String objectName;

        @Schema(description = "中文名")
        private String objectLabel;

        @Schema(description = "是否数组")
        private String isArr;

        @Schema(description = "数据类型")
        private String dataType;

        @Schema(description = "是否删除")
        private String isDelete;

        @Schema(description = "子项", example = "null")
        private List<InsideSqlOutputVO> children;


    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "外部数据服务")
    public static class DataModelOutsideServerVO implements Serializable {
        @Schema(description = "外数服务ID")
        private Long outId;

        @Schema(description = "外数服务编码")
        private String outCode;

        @Schema(description = "外数服务名称")
        private String outName;

        @Schema(description = "是否使用根对象")
        private Boolean isUseRootObject;
    }

}
