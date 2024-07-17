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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 模板动态DTO
 *
 * @author wiseco
 */
@Schema(description = "衍生变量模板动态DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VarTemplateDynamicInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "类型 varProcess:变量 commonFunction:公共函数", example = "varProcess")
    private String type;

    @Schema(description = "公共函数子类型 varTemplate-变量模板，publicMethod-公共方法,preProcess-数据预处理", example = "1")
    private String functionSubType;

    @Schema(description = "空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量ID", required = true, example = "1")
    private Long variableId;

    @Schema(description = "函数ID", required = true, example = "1")
    private Long functionId;

    @Schema(description = "查询路径", required = true, example = "input.contactInfo")
    private String dataValue;

    @Schema(description = "this字段对应的全路径", required = true, example = "input.contactInfo")
    private String fullPathValue;

    @Schema(description = "当前会话id:保持本地变量/参数时返回的sessionid", example = "A100001")
    private String sessionId;

    @Schema(description = "追加变量路径", required = true, example = "[\"input.application.age\",\"input.application.age\"]")
    private List<String> loopDataValues;
}
