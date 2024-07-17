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

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 变量保存输入参数 DTO
 *
 * @author wangxianli
 */
@Schema(description = "变量复制DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableCopyInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "来源变量的ID", example = "1")
    private Long copyId;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量名", required = true, example = "abc")
    @NotEmpty(message = "变量名不能为空")
    private String name;

    @Schema(description = "中文名", example = "人行征信评分卡")
    private String label;

}
