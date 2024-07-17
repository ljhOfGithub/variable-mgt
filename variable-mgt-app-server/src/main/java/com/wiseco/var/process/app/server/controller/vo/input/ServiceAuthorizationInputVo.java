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

import com.wiseco.model.common.entity.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "服务授权列表入参")
public class ServiceAuthorizationInputVo extends PageDto implements Serializable {

    private static final long serialVersionUID = -1932937088524741294L;

    @Schema(description = "调用方名称")
    private String      caller;

    @Schema(description = "所属部门")
    private String      callerDept;

    @Schema(description = "是否启用")
    private Boolean      enabled;

    @Schema(description = "创建部门")
    private String      createdDept;
}
