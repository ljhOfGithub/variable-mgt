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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实时服务授权查询入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "实时服务授权查询输入参数")
public class VariableServiceAuthorizationQueryInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = -1932937088524741294L;

    @Schema(description = "实时服务 ID", required = true)
    private Long serviceId;

    @Schema(description = "决策领域编码/名称关键词")
    private String keywords;
}
