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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 输出给用户进行指标选择的(用于监控报表, 属于公共服务接口)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "输出给用户进行指标选择的(用于监控报表, 属于公共服务接口)")
public class VariableListVo implements Serializable {

    private static final long serialVersionUID = 7515148104589037824L;

    @Schema(description = "变量的Id", required = true)
    private Long variableId;

    @Schema(description = "变量名称", required = true)
    private String label;

    @Schema(description = "变量编码", required = true)
    private String code;

    @Schema(description = "变量分类", required = true)
    private String category;

    @Schema(description = "数据类型", required = true)
    private String dataType;

    @Schema(description = "版本号", required = true)
    private Integer version;

    @Schema(description = "创建部门", required = true)
    private String createDept;
}
