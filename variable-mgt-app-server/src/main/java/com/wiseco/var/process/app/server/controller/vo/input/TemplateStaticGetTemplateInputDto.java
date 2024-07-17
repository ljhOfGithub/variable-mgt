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

import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 模板静态接口
 *
 * @author wiseco
 */
@ApiModel(value = "模板静态DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TemplateStaticGetTemplateInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "类型 varProcess:变量 commonFunction:公共函数", example = "1")
    private String type;

    @Schema(description = "公共函数子类型 varTemplate-变量模板，publicMethod-公共方法,preProcess-数据预处理", example = "1")
    private String functionSubType;

    @Schema(description = "空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量ID", required = true, example = "1")
    private Long variableId;

    @Schema(description = "函数ID", required = true, example = "1")
    private Long functionId;

    @Schema(description = "变量清单ID", required = true, example = "1")
    private Long manifestId;

    @Schema(description = "当前会话id:保持本地变量/参数时返回的sessionid", example = "A100001")
    private String sessionId;

    @Schema(description = "自定义函数返回数据类型：int、double、string、boolean、date、datetime", example = "1")
    private DataTypeEnum returnType;

    @ApiModelProperty(value = "模板唯一编码", example = "1")
    private String identifier;

    @ApiModelProperty(value = "模板名称", required = true, example = "")
    private String templateName;

    @ApiModelProperty(value = "dataValue", required = true, example = "")
    private List<String> dataValues;

}
