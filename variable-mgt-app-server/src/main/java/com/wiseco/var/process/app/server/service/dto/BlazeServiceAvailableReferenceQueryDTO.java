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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: fudengkui
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlazeServiceAvailableReferenceQueryDTO implements Serializable {

    /**
     * 领域ID
     */
    @Schema(description = "领域ID", example = "1001", required = true)
    private Long domainId;

    /**
     * 策略ID
     */
    @Schema(description = "策略ID", example = "1001", required = true)
    private Long strategyId;

    /**
     * 服务状态
     */
    @Schema(description = "服务状态", example = "服务状态：1=启用，2=停用，3=删除")
    private List<Integer> status;

    /**
     * 引入状态
     */
    @Schema(description = "引入状态", example = "引入状态：0=未引入，1=已引入")
    private List<Integer> refStatus;

    /**
     * 模块名称/模块编码
     */
    @Schema(description = "模块名称/模块编码", example = "授信模块")
    private String nameOrCode;

}
