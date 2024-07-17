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

/**
 * @description: 外数查询条件、结果表头设置
 * @author: liusiyu
 * @DateTime: 2024-02-27 10:43:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查询条件，表头列查询 出参DTO")
public class ConditionSettingOutputDto {

    @Schema(description = "查询条件id")
    private Long id;

    @Schema(description = "变量名")
    private String varName;

    @Schema(description = "变量中文名")
    private String varNameCn;

    @Schema(description = "变量所属 查询条件列还是结果表头列，0：查询条件，1：结果表头，2：都存在", example = "0")
    private Integer varType;

    @Schema(description = "查询条件列是否展示，1是，0否", example = "0")
    private Integer queryDisplay;

    @Schema(description = "查询条件列排序权重，越小越靠前")
    private Integer queryWeight;

    @Schema(description = "结果表头列是否展示，1是，0否")
    private Integer columnDisplay;

    @Schema(description = "结果表头列排序权重，越小越靠前")
    private Integer columnWeight;

    @Schema(description = "是否锁定，1是，0否")
    private Integer isLock;

    @Schema(description = "是否锁定在列头，1是，0否")
    private Integer isLockHead;

    @Schema(description = "是否可以取消选中，0：可以取消选中，1：不可取消选中")
    private Integer alwaysSelected;

    @Schema(description = "是否清单的可搜索变量，0：不是，1：是")
    private Integer isManiSearchVar;

}
