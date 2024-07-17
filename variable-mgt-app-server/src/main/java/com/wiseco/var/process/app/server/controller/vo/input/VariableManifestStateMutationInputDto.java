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
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 变量清单状态变更入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
@Data
@Schema(description = "变量清单状态变更输入参数")
public class VariableManifestStateMutationInputDto implements Serializable {

    private static final long serialVersionUID = 7786278302679481035L;

    @Schema(description = "变量空间 ID", required = true)
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "变量清单 ID", required = true)
    @NotNull(message = "变量清单 ID 不能为空")
    private Long manifestId;

    @Schema(description = "操作类型编码", example = "0: 新建, 1: 提交审核, 2: 审核通过, 3: 审核拒绝, 4: 退回编辑, 5: 停用, 6: 重新启用, 7: 删除, 8：导入, 9: 申请上线, 10: 审批拒绝, 11: 审批通过", required = true)
    @NotNull(message = "未定义变量清单操作")
    private Integer actionType;

    @Schema(description = "审核意见-仅当操作类型为审核通过 (4) 或审核拒绝 (5) 时传入")
    private String approDescription;
}
