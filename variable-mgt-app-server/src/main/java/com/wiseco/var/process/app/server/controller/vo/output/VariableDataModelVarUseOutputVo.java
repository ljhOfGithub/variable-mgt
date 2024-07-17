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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author: wangXianLi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "数据模型变量引用 出参Vo")
public class VariableDataModelVarUseOutputVo {

    @Schema(description = "标题", example = "变量使用")
    private String title;

    @Schema(description = "表头", example = "[{label:'服务名称',prop:'serviceName'},{label:'细分名称',prop:'bucketName'},{label:'策略名称',prop:'strategyName'},{label:'组件名称',prop:'componentName'},]")
    private List<TableHeader> tableHeader;

    /**
     * 尝试使用一个jsonObject进行所有的表内容填充
     */
    @Schema(description = "内容", example = "null")
    private List<JSONObject> tableData;

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
        //wxs在“变量”使用中这个变量指的是“变量”编码
        private String name;

        @Schema(description = "变量中文名", example = "")
        //wxs在“变量”使用中这个变量指的是“变量”名称
        private String label;

        @Schema(description = "版本", example = "")
        private String version;

        @Schema(description = "状态", example = "")
        private String status;

        @Schema(description = "分类", example = "")
        //wxs任何分类都走这个字段
        private String allClass;

        @Schema(description = "操作", example = "")
        private String operat;

        @Schema(description = "使用方式", example = "r")
        private String useWay;

    }

}
