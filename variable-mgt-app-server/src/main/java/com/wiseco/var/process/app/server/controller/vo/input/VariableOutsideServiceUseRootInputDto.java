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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量外部服务使用外数根对象入参 DTO
 *
 * @author kangyankun
 * @since 2022/09/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量外部服务引入配置输入参数")
public class VariableOutsideServiceUseRootInputDto implements Serializable {

    private static final long serialVersionUID = 8182008212544560814L;

    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @Schema(description = "外部服务 ID", required = true)
    private Long outsideServiceId;

}
