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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Schema(description = "获取内部数据表字段的DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BacktrackingTableFieldInputVO implements Serializable {

    @Schema(description = "数据表", example = "1")
    private List<TableNameInfo> tableNameInfos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "数据表明信息")
    public static class TableNameInfo implements Serializable {

        @Schema(description = "数据表名", required = true, example = "abc")
        @NotEmpty(message = "数据表名不能为空")
        private String tableName;

        @Schema(description = "数据表别名", required = true, example = "abc")
        @NotEmpty(message = "数据表别名不能为空")
        private String tableAlias;

    }

}

