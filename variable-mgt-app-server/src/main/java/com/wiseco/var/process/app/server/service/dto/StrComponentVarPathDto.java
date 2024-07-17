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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wangxianli
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "根据变量路径和数据模型ID或者策略ID 返回对象")
public class StrComponentVarPathDto {

    @Schema(description = "策略id", example = "")
    private Long strategyId;

    @Schema(description = "组件id", example = "1")
    private Long componentId;

    @Schema(description = "变量路径", example = "")
    private String varPath;

    @Schema(description = "变量名称", example = "")
    private String varName;

    @Schema(description = "数据类型", example = "")
    private String varType;

    @Schema(description = "是否数组", example = "0")
    private Integer isArray;

    @Schema(description = "参数/本地变量数据类型", example = "")
    private String parameterType;

    @Schema(description = "参数/本地变量是否数组", example = "0")
    private Integer isParameterArray;

    @Schema(description = "读写操作记录", example = "")
    private String actionHistory;

}
