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

import com.wiseco.var.process.app.server.commons.enums.VarUpdateActionEnum;
import com.wiseco.var.process.app.server.service.dto.VariableTagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量修改变量信息DTO
 * @author Gmm
 */
@Schema(description = "批量修改变量信息 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableBatchSaveInputDto {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", example = "1")
    private Long              spaceId;

    @Schema(description = "变量ID列表", example = "[1,2,3]")
    private List<Long> variableIdList;

    @Schema(description = "变量更新操作类型", example = "TAGS")
    private VarUpdateActionEnum updateAction;

    @Schema(description = "用户标签")
    private List<VariableTagDto> tags;



}
