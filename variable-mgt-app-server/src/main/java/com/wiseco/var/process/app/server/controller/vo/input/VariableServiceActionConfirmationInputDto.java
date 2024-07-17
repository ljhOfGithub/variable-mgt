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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 实时服务操作确认入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
@Data
@Schema(description = "实时服务操作确认输入参数")
public class VariableServiceActionConfirmationInputDto implements Serializable {

    private static final long serialVersionUID = 7786278302679481035L;

    @Schema(description = "空间 ID", required = true)
    @NotNull(message = "空间 ID 不能为空。")
    private Long spaceId;

    @Schema(description = "服务 ID", required = true)
    @NotNull(message = "服务 ID 不能为空。")
    private Long serviceId;

    @Schema(description = "操作", allowableValues = "发布: start, 删除: delete, 停用: stop, 重新发布: restart，申请发布：apply，退回编辑：returnEdit", required = true)
    @NotEmpty(message = "操作未定义。")
    private String action;
}
