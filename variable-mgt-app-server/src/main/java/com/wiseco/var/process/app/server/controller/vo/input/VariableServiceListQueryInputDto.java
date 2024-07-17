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
import java.io.Serializable;
import java.util.List;

/**
 * 实时服务接口列表查询入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
@Data
@Schema(description = "实时服务接口列表查询输入参数")
public class VariableServiceListQueryInputDto implements Serializable {

    private static final long serialVersionUID = 8893288228978059895L;

    @Schema(description = "变量空间 ID")
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "实时服务类型", example = "1: 实时, 2: 批量")
    private List<Integer> serviceType;

    @Schema(description = "服务名称/编码搜索关键词")
    private String keyword;
}
