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
package com.wiseco.var.process.app.server.service.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: fudengkui
 * @since : 2023-02-21 13:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "属性DTO")
public class AttributeDTO implements Serializable {

    @Schema(description = "attribute编号", type = "String", example = "0005DAF3E2D7B200")
    private String identifier;

    @Schema(description = "class编号", type = "String", example = "0005DAF3E2D7B200")
    private String classIdentifier;

    @NotEmpty(message = "属性名不能为空")
    @Schema(description = "属性名", required = true, type = "String", example = "code")
    private String name;

    @NotEmpty(message = "显示名不能为空")
    @Schema(description = "显示名", required = true, type = "String", example = "code")
    private String label;

    @Schema(description = "java类型", required = true, type = "String", example = "String")
    private String javaType;

    @Schema(description = "WRL类型", required = true, type = "String", example = "string")
    private String wrlType;

    @Schema(description = "属性类型是否数组：0=否，1=是", required = true, type = "Integer", example = "1")
    private Integer typeIsArray;

    @Schema(description = "修饰符", required = true, type = "Integer", example = "1")
    private Integer modifier;

    @Schema(description = "访问：read/write，readonly", required = true, type = "String", example = "readonly")
    private String access;

    @Schema(description = "属性来源：1=字段，2=方法", required = true, type = "Integer", example = "1")
    private Integer sourceType;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0=停用，1=启用", required = true, type = "Integer", example = "1")
    private Integer status;

    @NotNull(message = "导入状态不能为空")
    @Schema(description = "导入状态：0=未导入，1=已导入", required = true, type = "Integer", example = "1")
    private Integer importStatus;

}
