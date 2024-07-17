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

import com.wiseco.boot.data.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Asker.J
 * @since 2022/9/7
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量查询入参")
public class VarQueryReqVO extends PageDTO {

    @Schema(description = "空间主键id")
    private Long workspaceId;

    @Schema(description = "变量类型主键")
    private Long varTypeId;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "变量角色")
    private String role;

    @Schema(description = "变量来源")
    private String source;

    @Schema(description = "搜索框（中文名/英文名）")
    private String search;
}
