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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * @author: xiewu
 * @since: 2021/12/2
 */
@Data
@Builder
@Schema(description = "根据模型id和类型获取输入输出变量树 入参DTO")
@NoArgsConstructor
@AllArgsConstructor
public class DataModelIdGetInputOutputVarTreeInputDto {

    @Schema(description = "数据模型id", required = true, example = "1")
    @NotNull(message = "数据模型id不能为空")
    private Long dataModelId;

    @Schema(description = "位置入参", required = true, example = "['input','output']")
    @NotNull(message = "位置入参不能为空")
    private List<String> positionList;

    @Schema(description = "类型入参", example = "['int','double','boolean']")
    private List<String> typeList;
}
