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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量空间磁贴输出参数 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量空间磁贴输出参数 DTO")
public class VarProcessSpaceTileOutputDto implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "空间 ID")
    private Long id;

    @Schema(description = "空间名称")
    private String name;

    @Schema(description = "空间编码")
    private String code;

    @Schema(description = "空间描述")
    private String description;

    @Schema(description = "上架变量数")
    private Integer listedVariableNumber;

    @Schema(description = "发布服务数")
    private Integer releasedServiceNumber;

    @Schema(description = "最后编辑时间", example = "2022-06-07 18:00:00")
    private String updatedTime;
}
