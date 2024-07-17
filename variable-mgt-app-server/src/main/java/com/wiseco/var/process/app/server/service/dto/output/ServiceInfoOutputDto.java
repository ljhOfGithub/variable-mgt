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
package com.wiseco.var.process.app.server.service.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 服务信息的输出结构
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "服务信息的输出结构")
public class ServiceInfoOutputDto implements Serializable {

    private static final long serialVersionUID = 4711291259677256901L;

    @Schema(description = "服务ID", required = true, example = "1")
    private Long id;

    @Schema(description = "实时服务名称", required = true, example = "变量加工服务")
    private String name;

    @Schema(description = "服务编码", required = true, example = "PBOC")
    private String code;

    @Schema(description = "服务分类", required = true, example = "信用卡审批")
    private String serviceCategoryName;

    @Schema(description = "版本号", required = true, example = "1")
    private Integer version;

    @Schema(description = "变量清单名称", required = true, example = "变量加工")
    private List<String> manifestNames;

    @Schema(description = "状态", required = true, example = "EDITING")
    private VarProcessServiceStateEnum state;

    @Schema(description = "审核拒绝信息")
    private String statusDescription;

    @Schema(description = "创建部门", required = true, example = "营销部")
    private String createDepartment;

    @Schema(description = "创建人", required = true, example = "张三")
    private String createdUser;

    @Schema(description = "创建时间", required = true, example = "2023-08-14 17:28:36")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "最近编辑人", required = true, example = "李四")
    private String updateUser;

    @Schema(description = "更新时间", required = true, example = "2023-08-15 17:28:36")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    @Schema(description = "子服务(code相同，但版本不同)", required = true)
    private List<ServiceInfoOutputDto> children;
}
