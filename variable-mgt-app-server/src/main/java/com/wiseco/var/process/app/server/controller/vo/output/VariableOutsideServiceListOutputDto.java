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

import com.wiseco.var.process.app.server.service.dto.OperationButton;
import com.wiseco.var.process.app.server.service.dto.VariableOutsideRefServiceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 变量外部服务列表出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量外部服务列表输出参数")
public class VariableOutsideServiceListOutputDto implements Serializable {

    private static final long serialVersionUID = -1592333970406413349L;

    @Schema(description = "外部服务 ID")
    private Long outsideServiceId;

    @Schema(description = "接收对象记录 ID (变量-外部服务引入对象表主键 ID)")
    private Long receiverObjectRecordId;

    @Schema(description = "外部服务名称")
    private String name;

    @Schema(description = "外部服务编码")
    private String code;

    @Schema(description = "服务类型", example = "1: 行内数据获取, 2: 三方数据调用")
    private Integer type;

    @Schema(description = "缓存时间")
    private String dataCacheTime;

    @Schema(description = "服务状态", example = "0: 编辑中, 1: 启用, 2: 停用")
    private Integer outsideServiceState;

    @Schema(description = "引入状态", example = "0: 未引入 1: 已引入")
    private Integer referenceState;

    @Schema(description = "引入人")
    private String createdUser;

    @Schema(description = "引入时间", example = "2021-12-30 12:00:00")
    private String createdTime;

    @Schema(description = "接受对象名")
    private String receiverObjectName;

    @Schema(description = "接受对象中文名")
    private String receiverObjectLabel;

    @Schema(description = "操作")
    private List<OperationButton> operationButton;

    @Schema(description = "引入服务数", example = "10")
    private Integer referenceNumber;

    @Schema(description = "引入服务数列表", example = "10")
    private List<VariableOutsideRefServiceDto> referenceList;
}
