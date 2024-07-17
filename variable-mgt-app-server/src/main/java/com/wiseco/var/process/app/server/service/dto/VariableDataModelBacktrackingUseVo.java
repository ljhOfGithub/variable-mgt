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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: xiewu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "根据变量路径查询变量或公共函数 返回对象")
public class VariableDataModelBacktrackingUseVo {

    @Schema(description = "任务名称", example = "1")
    private String name;

    @Schema(description = "任务状态", example = "1")
    private String state;

}
