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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangxianli
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "实时服务流程图节点 输出dto")
public class VariableServiceFlowNodesOutputDto implements Serializable {

    @Schema(description = "节点ID", example = "")
    @JsonProperty("node_id")
    private String nodeId;

    @Schema(description = "节点类型：task-变量加工，parallel-分支，service-外部服务", example = "")
    private String type;

    @Schema(description = "节点名称", example = "")
    private String label;

    @Schema(description = "分支", example = "null")
    private List<Branches> branches;

    @Schema(description = "外部服务名称", example = "")
    @JsonProperty("service_name")
    private String serviceName;

    @Schema(description = "外部服务编号", example = "")
    @JsonProperty("service_code")
    private String serviceCode;

    @Schema(description = "参数", example = "null")
    @JsonProperty("input_parameter_bindings")
    private List<ParameterModel> inputParameterBindings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分支")
    public static class Branches implements Serializable {

        @Schema(description = "分支ID", example = "")
        @JsonProperty("branches_id")
        private String branchesId;

        @Schema(description = "分支名称", example = "")
        private String label;

        @Schema(description = "分支节点", example = "")
        private List<VariableServiceFlowNodesOutputDto> nodes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "参数")
    public static class ParameterModel implements Serializable {

        @Schema(description = "变量全路径", example = "")
        private String mapping;

        @Schema(description = "中文名", example = "")
        private String cnName;

        @Schema(description = "变量名", example = "")
        private String name;

        @Schema(description = "是否数组", example = "")
        private String isArr;

        @Schema(description = "数据类型", example = "")
        private String type;

    }

}
