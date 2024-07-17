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
package com.wiseco.var.process.app.server.service.dto.innerdata;

import com.wiseco.var.process.app.server.commons.enums.JoinEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Asker.J
 * @since  2022/11/2
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "关联表信息")
public class TableJoinDto {

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表别名")
    private String alias;

    @Schema(description = "关联类型，主表没有这个配置")
    private JoinEnum joinType;

    @Schema(description = "关联条件")
    private String joinField;

    /**
     * 中间处理保存原表字段
     */
    @Schema(description = "中间处理保存原表字段", hidden = true)
    private List<TableFieldDto> fieldDtoList;

}
