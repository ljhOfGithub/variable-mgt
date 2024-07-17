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
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 实时服务的状态更改输入实体对象-控制层
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "实时服务的状态更改输入实体对象-控制层")
public class VariableServiceUpdateInputVO implements Serializable {

    private static final long serialVersionUID = 2070457580256428600L;

    @NotNull(message = "服务空间ID不能为空")
    @Schema(description = "服务空间ID", example = "1")
    private Long spaceId;

    @NotNull(message = "服务ID不能为空")
    @Schema(description = "服务ID", example = "21000")
    private Long serviceId;

    @Schema(description = "版本id")
    @NotNull(message = "请传入版本id")
    private Long versionId;

    @NotNull(message = "操作类型不能为空!")
    @Schema(description = "操作类型：1-提交；2-审核通过；3-审核拒绝；4-退回编辑；5-停用；6-(重新)启用", example = "2")
    private Integer actionType;

    @Size(max = 500, message = "审核意见不得超过500字符!")
    @Schema(description = "审核意见-仅当操作类型为审时传入")
    private String approDescription;
}
