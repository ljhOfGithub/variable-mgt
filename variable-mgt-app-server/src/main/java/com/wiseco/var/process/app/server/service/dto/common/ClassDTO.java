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
import java.util.Date;

/**
 * @author: fudengkui
 * @since : 2023-02-21 15:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "类DTO")
public class ClassDTO implements Serializable {

    @Schema(description = "class编号", type = "String", example = "0005DAF3E2D7B200")
    private String identifier;

    @Schema(description = "jar包编号", type = "String", example = "0005DAF3E2D7B200")
    private String jarIdentifier;

    @NotEmpty(message = "类名称不能为空")
    @Schema(description = "类名称", required = true, type = "String", example = "ClaimSummary")
    private String name;

    @NotEmpty(message = "全路径类名称不能为空")
    @Schema(description = "全路径类名称", required = true, type = "String", example = "claim.model.ClaimSummary")
    private String canonicalName;

    @Schema(description = "全路径类名称是否存在", required = true, type = "Boolean", example = "true")
    private Boolean canonicalNameExists;

    @Schema(description = "类名称显示名", required = true, type = "String", example = "ClaimSummary")
    private String label;

    @Schema(description = "归属包", required = true, type = "String", example = "mdiacalClaimXom.jar")
    private String jarName;

    @Schema(description = "class类型：1=class，2=abstract class，3=interface，4=enum", required = true, type = "Integer", example = "1")
    private Integer classType;

    @Schema(description = "修饰符", required = true, type = "Integer", example = "1")
    private Integer modifier;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0=停用，1=启用", required = true, type = "Integer", example = "1")
    private Integer status;

    @NotNull(message = "导入状态不能为空")
    @Schema(description = "导入状态：0=未导入，1=已导入", required = true, type = "Integer", example = "1")
    private Integer importStatus;

    @Schema(description = "class业务类型：1=class有属性也有方法，2=class有属性无方法，3=class无属性有方法，4=class无属性无方法", required = true, type = "Integer", example = "1")
    private Integer classBizType;

    @Schema(description = "创建人", required = true, type = "String", example = "张三")
    private String createdUser;

    @Schema(description = "创建时间", required = true, type = "Date", example = "2023-02-23 18:00:00")
    private Date createdTime;

    @Schema(description = "编辑人", required = true, type = "String", example = "李四")
    private String updatedUser;

    @Schema(description = "编辑时间", required = true, type = "Date", example = "2023-02-23 18:00:00")
    private Date updatedTime;
}
