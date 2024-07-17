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
package com.wiseco.var.process.app.server.controller.vo;

import com.wiseco.var.process.app.server.controller.vo.input.DataModelMatchTreeInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Schema(description = "清单匹配树参数DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManifestModelMatchTreeInputDto extends DataModelMatchTreeInputDto {

    private static final long serialVersionUID = 3137457580256485647L;

    @Schema(description = "清单id", required = true, example = "1")
    @NotNull(message = "清单ID不能为空")
    private Long manifestId;

    @Schema(description = "数据模型名称",example = "aaa")
    private String objectName;
}
