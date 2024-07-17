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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 变量空间属性 输出参数 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/10
 */
@Data
@Builder
@Schema(description = "变量空间属性")
public class VarProcessSpacePropertiesOutputDto implements Serializable {

    private static final long serialVersionUID = 101067704755666311L;

    @Schema(description = "变量空间编码")
    private String code;

    @Schema(description = "变量空间名称")
    private String name;

    @Schema(description = "变量空间描述")
    private String description;

    @Schema(description = "创建人")
    private String createdUser;

    @Schema(description = "创建时间")
    private String createdTime;

    @Schema(description = "最后编辑人")
    private String updatedUser;

    @Schema(description = "编辑时间")
    private String updatedTime;
}
