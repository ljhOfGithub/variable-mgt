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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量外部服务引入配置入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量外部服务引入配置输入参数")
public class VariableOutsideServiceReferenceConfigInputDto implements Serializable {

    private static final long serialVersionUID = 8182008212544560814L;

    @Schema(description = "变量空间 ID")
    private Long spaceId;

    @Schema(description = "外部服务 ID")
    private Long outsideServiceId;

    /**
     * 入参对象绑定结构详见 Wiki
     */
    @Schema(description = "入参对象绑定内容 (JSON 对象)")
    private JSONObject content;

    @Schema(description = "接收对象名")
    private String receiverObjectName;

    @Schema(description = "接收对象中文名")
    private String receiverObjectLabel;

    @Schema(description = "是否保存到数据模型: 1:是 0:否")
    private Integer isSaveDataModal;

    @Schema(description = "是否使用根对象: 1:是 0:否")
    private Integer isUseRootObject;
}
