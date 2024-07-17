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
import com.wiseco.var.process.app.server.service.dto.OperationButton;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 根据领域id查询策略参数列表
 *
 * @author: zhouxiuxiu
 * @since: 2021/11/8 14:25
 */
@Data
@Schema(description = "查询系统参数列表 出参DTO")
public class SysParamListOutputDto {

    @Schema(description = "主键id", example = "1")
    private Long id;

    @Schema(description = "参数名", example = "initPwd")
    private String paramName;

    @Schema(description = "参数中文名", example = "用户初始密码")
    @NotEmpty(message = "参数中文名不能为空！")
    private String paramNameCn;

    @Schema(description = "数据类型：int、double、string、boolean、date、datetime", example = "string")
    private String dataType;

    @Schema(description = "参数类型：1内置参数 2自定义参数", example = "2")
    private Integer paramType;

    @Schema(description = "参数值", example = "123456")
    private String paramValue;

    @Schema(description = "自定义系统参数使用标识 0:内置类型不显示 1:未使用 2:已使用", example = "123456")
    private Integer usedFlag;

    @Schema(description = "最后编辑人", example = "123456")
    private String updatedUser;

    @Schema(description = "编辑时间", example = "2021-12-30 12:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "操作数据", example = "[{value:'sysParamEdit',label:'编辑'},{value:'sysParamDelete',label:'删除'}]")
    private List<OperationButton> operationButton;
}
