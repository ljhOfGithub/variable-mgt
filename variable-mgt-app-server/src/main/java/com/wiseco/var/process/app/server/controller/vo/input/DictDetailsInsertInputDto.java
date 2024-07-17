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
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: xiewu
 */
@Data
@Schema(description = "字典详情新增或者修改 入参DTO")
public class DictDetailsInsertInputDto {

    @Schema(description = "主键id：如果有id则新增，没有id则修改", example = "1")
    private Long id;

    @Schema(description = "变量空间id", example = "1")
    private Long spaceId;

    @Schema(description = "字典类型id", example = "1")
    @NotNull(message = "字典类型id不能为空！")
    private Long dictId;

    @Schema(description = "字典项编码", example = "yzf")
    @NotNull(message = "字典项编码不能为空！")
    private String code;

    @Schema(description = "字典项名称", example = "翼支付")
    @NotNull(message = "字典项名称不能为空！")
    private String name;

    @Schema(description = "字典项父编码", example = "jt360")
    private String parentCode;

    @Schema(description = "状态 0:停用 1:启用", example = "1")
    private Integer state;

}