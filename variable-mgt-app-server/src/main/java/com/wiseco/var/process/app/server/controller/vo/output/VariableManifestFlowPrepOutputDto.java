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
 * 流程信息 DTO
 *
 * @author wangxianli
 * @since 2022/9/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "预处理逻辑输出参数")
public class VariableManifestFlowPrepOutputDto implements Serializable {

    @Schema(description = "identifier", example = "null")
    private String identifier;

    @Schema(description = "名称", example = "null")
    private String name;

    @Schema(description = "对象名称", example = "null")
    private String objectName;

    @Schema(description = "版本号", example = "null")
    private Integer version;

    @Schema(description = "创建部门")
    private String dept;
}
