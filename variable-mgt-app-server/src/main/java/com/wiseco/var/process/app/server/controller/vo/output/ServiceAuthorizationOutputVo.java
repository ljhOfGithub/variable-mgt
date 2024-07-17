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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class ServiceAuthorizationOutputVo {

    @Schema(description = "授权id")
    private Long id;

    @Schema(description = "调用方")
    private String      caller;

    @Schema(description = "所属部门")
    private String      callerDept;

    @Schema(description = "所属部门code")
    private String      callerDeptCode;

    @Schema(description = "授权说明")
    private String      details;

    @Schema(description = "状态")
    private Boolean      enabled;

    @Schema(description = "创建部门")
    private String      createdDept;

    @Schema(description = "更新人")
    private String      updatedUser;

    @Schema(description = "创建人")
    private String      createdUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "最近编辑时间")
    private Date updatedTime;
}
