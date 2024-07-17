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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 数据模型操作校验 入参 Vo
 *
 * @author wangxianli
 * @since 2022/8/30
 */
@Data
@Schema(description = "数据模型操作校验输入参数")
public class VariableDataModelUseListInputVo implements Serializable {

    @Schema(description = "变量空间 ID", example = "90000000")
    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    @Schema(description = "对象名称", example = "pboc")
    @NotBlank(message = "对象名称不能为空")
    private String objectName;

    @Schema(description = "操作类型：0-编辑，1-删除", example = "0")
    @NotNull(message = "操作类型不能为空")
    private Integer actionType;

}
