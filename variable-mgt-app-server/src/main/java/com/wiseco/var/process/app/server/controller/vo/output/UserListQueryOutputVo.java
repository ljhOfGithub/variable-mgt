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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wuweikang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserListQueryOutputVo  implements Serializable {

    private static final long serialVersionUID = 8799865908944970102L;
    @Schema(description = "用户Id")
    private Integer userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户姓名")
    private String fullName;

    @Schema(description = "部门")
    private String deptName;
}
