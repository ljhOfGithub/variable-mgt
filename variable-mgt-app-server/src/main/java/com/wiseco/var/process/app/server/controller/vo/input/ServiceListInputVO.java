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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * controller层中，实时服务的分页+条件查询入参VO
 */

@Data
@Schema(description = "controller层中，实时服务的分页+条件查询入参VO")
public class ServiceListInputVO implements Serializable {

    private static final long serialVersionUID = 2014446166832341148L;

    @Schema(description = "服务分类的ID")
    private Long serviceCategoryId;

    @Schema(description = "变量清单ID")
    private Long manifestId;

    @Schema(description = "部门code")
    private String deptCode;

    @Schema(description = "服务名称/服务编码")
    @Size(max = 128, message = "字段过长")
    private String serviceNameOrServiceCode;

    @Schema(description = "每一页大小",example = "10",required = true)
    @Min(value = 1, message = "每一页大小不能小于1")
    @NotNull(message = "每一页大小不能为空")
    private Integer pageSize;

    @Schema(description = "当前页码", required = true)
    @Min(value = 1, message = "当前页码必须大于等于1")
    @NotNull(message = "当前页码不能为空")
    private Integer currentPage;
}
