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

import com.wiseco.boot.commons.data.PageDTO;
import com.wiseco.var.process.app.server.enums.SceneStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "场景列表入参dto")
@AllArgsConstructor
@NoArgsConstructor
public class SceneListInputDto extends PageDTO {
    @Schema(description = "状态",allowableValues = {"ENABLED","DISABLED"})
    private SceneStateEnum state;
    @Schema(description = "部门编码")
    private String deptCode;
    @Schema(description = "名称/编码查询关键字")
    private String keyword;
    @Schema(description = "数据模型名称")
    private String dataModelName;
}
