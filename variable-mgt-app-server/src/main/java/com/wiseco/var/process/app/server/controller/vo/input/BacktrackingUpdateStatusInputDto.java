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

import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量回溯状态修改 DTO
 *
 * @author liutong
 */
@Schema(description = "修改批量回溯状态 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BacktrackingUpdateStatusInputDto {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "批量回溯ID", example = "1")
    private Long id;

    @Schema(description = "操作类型：2-申请上架，3-下架，4-上架，6-审核通过，7-审核拒绝，8-退回编辑，9-删除", example = "2")
    private FlowActionTypeEnum actionType;

    @Schema(description = "描述", example = "1")
    private String description;

    /**
     * 获取空间id
     * @return 空间id
     */
    public Long getSpaceId() {
        spaceId = 1L;
        return spaceId;
    }
}
