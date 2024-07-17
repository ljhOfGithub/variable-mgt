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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "添加数据模型 全部信息 VO")
public class VariableDataModelAddNewInputVo implements Serializable {
    @Schema(description = "上一步信息")
    VariableDataModelAddNewNextInputVo firstPageInfo;
    @Schema(description = "空间ID", example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long spaceId;
    @Schema(description = "数据结构定义, 这个是用来保存第二步的数据的JSON")
    private DomainDataModelTreeDto content;
}
