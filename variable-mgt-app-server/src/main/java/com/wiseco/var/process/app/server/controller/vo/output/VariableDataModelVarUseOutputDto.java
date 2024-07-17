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

import java.util.List;

/**
 * @author: wangxianli
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "数据模型变量引用 出参DTO")
public class VariableDataModelVarUseOutputDto {

    @Schema(description = "标题", example = "变量使用")
    private String title;

    @Schema(description = "表头", example = "[{label:'服务名称',prop:'serviceName'},{label:'细分名称',prop:'bucketName'},{label:'策略名称',prop:'strategyName'},{label:'组件名称',prop:'componentName'},]")
    private List<TableHeader> tableHeader;

    @Schema(description = "表内容", example = "{serviceName:'授信',bucketName:'360借条',strategyName:'授信申请策略【开发】V1.0',componentName:'客户准入规则'}")
    private List<TableData> tableData;

    @SuperBuilder
    @Schema(description = "表头信息")
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class TableHeader {
        @Schema(description = "标题", example = "")
        private String label;
        @Schema(description = "字段名称", example = "")
        private String prop;
    }

    @SuperBuilder
    @Schema(description = "表内容")
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class TableData {
        @Schema(description = "变量名", example = "")
        private String name;

        @Schema(description = "变量中文名", example = "")
        private String label;

        @Schema(description = "版本", example = "")
        private String version;

        @Schema(description = "状态", example = "")
        private String status;

    }
}
