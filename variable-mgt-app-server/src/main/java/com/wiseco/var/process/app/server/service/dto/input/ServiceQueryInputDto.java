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
package com.wiseco.var.process.app.server.service.dto.input;

import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * service层中，实时服务的分页+条件查询入参Dto
 */

@Data
@Schema(description = "service层中，实时服务的分页+条件查询入参Dto")
public class ServiceQueryInputDto implements Serializable {

    private static final long serialVersionUID = 2010046100832340048L;

    @Schema(description = "服务分类的ID", required = true)
    private Long serviceCategoryId;

    @Schema(description = "变量清单ID", required = true)
    private Long manifestId;

    @Schema(description = "状态: EDITING-编辑中；PENDING_REVIEW-待审核；ENABLED-启用；DISABLED-停用；REJECTED-审核拒绝", required = true)
    private VarProcessServiceStateEnum state;

    @Schema(description = "创建部门ID", required = true)
    private Long createDepartmentId;

    @Schema(description = "服务名称/服务编码", required = true)
    private String serviceNameOrServiceCode;

    @Schema(description = "排序字段, name_asc(服务名称升序), name_desc(服务名称降序), code_asc(服务编码升序), code_desc(服务编码降序), createTime_asc(创建时间升序), createTime_desc(创建时间降序), updatedTime_asc(编辑时间升序), updatedTime_desc(编辑时间降序)", required = true)
    private String order;

    @Schema(description = "每一页大小", required = true)
    private Integer pageSize;

    @Schema(description = "当前页码", required = true)
    private Integer currentPage;
}
