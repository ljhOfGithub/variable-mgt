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

import com.wiseco.boot.data.PageDTO;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionHandleTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "公共函数查询 VO")
public class FunctionQueryInputVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "函数类型:变量模板，公共方法,数据预处理", example = "TEMPLATE")
    private FunctionTypeEnum functionType;

    @Schema(description = "预处理对象名称")
    private String dataModelName;

    @Schema(description = "名称", example = "征信")
    private String name;

    @Schema(description = "状态")
    private FlowStatusEnum functionStatus;

    @Schema(description = "是否使用")
    private Boolean isUse;

    @Schema(description = "是否测试")
    private Boolean isTest;

    @Schema(description = "部门编号")
    private String createdDeptCode;

    @Schema(description = "排序字段", example = "label_asc")
    private String order;

    @Schema(description = "模板分类Id")
    private Long categoryId;

    @Schema(description = "处理方式", example = "PYTHON")
    private FunctionHandleTypeEnum handleType;
}
