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

import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 变量清单-数据模型绑定参数 VO
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单-数据模型绑定输入, 输出参数")
public class VariableManifestDataModelMappingVo implements Serializable {

    private static final long serialVersionUID = -1238633535363023813L;

    @Schema(description = "数据模型对象id")
    private Long id;

    @Schema(description = "对象名称")
    private String name;

    @Schema(description = "对象中文名")
    private String description;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "数据来源")
    private VarProcessDataModelSourceType sourceType;

    @Schema(description = "来源表/外部服务")
    private String source;

    @Schema(description = "原始数据数")
    private Integer sourceNum;

    @Schema(description = "扩展数据数")
    private Integer extendNum;

    @Schema(description = "数据模型查询条件映射列表")
    private List<QueryCondition> queryConditionList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryCondition implements Serializable {

        @Schema(description = "数据模型变量名")
        private String varName;

        @Schema(description = "数据模型全路径变量名")
        private String fullPathValue;

        @Schema(description = "数据模型变量中文名")
        private String varNameCn;

        @Schema(description = "数据模型变量类型")
        private String varType;

        @Schema(description = "映射枚举的code")
        private String mappingCode;

        @Schema(description = "映射枚举的name")
        private String mappingName;
    }
}
