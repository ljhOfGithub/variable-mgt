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

import java.io.Serializable;

/**
 * 变量上架 DTO
 *
 * @author wangxianli
 */
@Schema(description = "修改变量状态 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableUpdateStatusInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", example = "1")
    private Long              spaceId;

    @Schema(description = "变量ID", example = "1")
    private Long              variableId;

    @Schema(description = "操作类型：2-申请上架，3-下架，4-上架，6-审核通过，7-审核拒绝，8-退回编辑，9-删除", example = "2")
    private Integer           actionType;

    @Schema(description = "描述", example = "1")
    private String            description;

    @Schema(description = "审核意见-仅当操作类型为审核通过 (6) 或审核拒绝 (7) 时传入")
    private String            approDescription;
}
