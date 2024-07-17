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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "服务版本信息")
public class ServiceVersionInfoOutputVo {

    @Schema(description = "版本id")
    private Long versionId;

    @Schema(description = "版本号",example = "V1")
    private String version;

    @Schema(description = "变量清单名称", example = "变量加工")
    private List<String> manifestNames;

    @Schema(description = "状态", allowableValues = {"EDITING","PENDING_REVIEW","ENABLED","DISABLED","REJECTED"},example = "EDITING")
    private VarProcessServiceStateEnum state;

    @Schema(description = "审核拒绝信息")
    private String approDescription;

    @Schema(description = "创建部门", example = "研发部")
    private String createDepartment;

    @Schema(description = "创建人", example = "张三")
    private String createdUser;

    @Schema(description = "创建时间", example = "2023-08-14 17:28:36")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "最近编辑人", example = "李四")
    private String updateUser;

    @Schema(description = "更新时间", example = "2023-08-15 17:28:36")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    @Schema(description = "版本描述")
    private String description;
}
