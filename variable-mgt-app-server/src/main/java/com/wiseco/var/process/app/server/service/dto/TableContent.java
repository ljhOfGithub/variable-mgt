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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

/**
 * 属性面板列表表格
 *
 * @author: zhouxx
 */
@Data
@Schema(description = "属性面板列表内容信息")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableContent {

    @Valid
    @Schema(description = "列表头信息", example = "版本号")
    List<TableHeadInfo> tableHead;
    @Schema(description = "列表内容", example = "V3.3")
    List<JSONObject> tableData;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "表格头")
    public static class TableHeadInfo {
        @Schema(description = "内容", example = "方案编码")
        private String lable;
        @Schema(description = "字段值", example = "code")
        private String key;

        @Valid
        @Schema(description = "交互类型")
        private TransInfo trans;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "交互类型")
    public static class TransInfo {
        @Schema(description = "类型", example = "link")
        private String type;
    }
}
