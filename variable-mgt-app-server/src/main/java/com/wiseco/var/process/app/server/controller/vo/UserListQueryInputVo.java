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
package com.wiseco.var.process.app.server.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wuweikang
 */
@Schema(description = "用户列表 vo")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserListQueryInputVo implements Serializable {

    private static final long serialVersionUID = 8799865908944970002L;

    @Schema(description = "部门id")
    private Integer deptId;

    @Schema(description = "用户名/用户姓名")
    private String name;
}
