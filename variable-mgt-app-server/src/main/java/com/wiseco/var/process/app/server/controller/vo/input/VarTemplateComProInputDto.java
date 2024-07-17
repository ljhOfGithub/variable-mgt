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

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 模板动态DTO
 *
 * @author wiseco
 */
@Schema(description = "对象一级属性对比模板动态DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VarTemplateComProInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "类型 varProcess:变量 commonFunction:公共函数", example = "1")
    private String type;

    @Schema(description = "空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量ID", required = true, example = "1")
    private Long variableId;

    @Schema(description = "函数ID", required = true, example = "1")
    private Long functionId;

    @Schema(description = "数据节点A", required = true, example = "input.contactInfo")
    private String dataValueA;
    @Schema(description = "this字段对应的全路径", required = true, example = "input.contactInfo")
    private String fullPathValueA;

    @Schema(description = "数据节点B", required = true, example = "parameters.name1")
    private String dataValueB;
    @Schema(description = "this字段对应的全路径", required = true, example = "input.contactInfo")
    private String fullPathValueB;

    @Schema(description = "当前会话id:保持本地变量/参数时返回的sessionid", example = "A100001")
    private String sessionId;

    @ApiModelProperty(value = "外部服务 ID", notes = "仅限外部服务节点请求数据绑定使用", example = "1")
    private Long outsideServiceId;
}
