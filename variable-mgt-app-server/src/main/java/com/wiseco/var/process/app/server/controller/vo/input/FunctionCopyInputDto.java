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

import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 公共函数保存输入参数 DTO
 *
 * @author wangxianli
 */
@Schema(description = "公共函数复制DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FunctionCopyInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "复制对象的ID", example = "1")
    private Long copyId;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "名称", required = true, example = "abc")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "函数类型:1-变量模板，2-公共方法,3-数据预处理", example = "1")
    @NotNull(message = "函数类型不能为空")
    private FunctionTypeEnum functionType;

}

