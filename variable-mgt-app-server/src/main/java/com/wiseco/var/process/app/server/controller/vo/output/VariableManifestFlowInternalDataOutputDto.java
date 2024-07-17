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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wangxianli
 */
@Schema(description = "流程引用内部数据DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableManifestFlowInternalDataOutputDto implements Serializable {
    private static final long serialVersionUID = 8668690652316747092L;

    @Schema(description = "内部数据名称", example = "申请信息")
    private String internalDataName;

    @Schema(description = "编号", example = "")
    private String identifier;

    /**
     * 入参映射
     */
    @Schema(description = "入参映射")
    private List<InputVarDto> inputList;

    /**
     * 出参类型
     */
    @Schema(description = "出参类型")
    private OutputObjectDto outputDto;

    @Data
    @SuperBuilder
    @Schema(description = "入参映射")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputVarDto {

        @Schema(description = "变量名称", example = "name")
        private String varName;

        @Schema(description = "参数名称", example = "客户名称")
        private String description;

        @Schema(description = "数据类型", example = "string")
        private String fieldType;

        @Schema(description = "是否数组 1是，0否", example = "1")
        private String isArr;
    }

    @Data
    @SuperBuilder
    @Schema(description = "接收对象信息")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputObjectDto {

        @Schema(description = "接收对象名称", example = "AppFico")
        private String refObjectName;

        @Schema(description = "接收对象名称中文名称", example = "AppFico中文")
        private String refObjectNameCn;

    }
}
