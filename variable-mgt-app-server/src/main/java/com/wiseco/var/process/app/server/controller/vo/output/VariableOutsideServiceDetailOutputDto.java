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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.service.dto.VariableOutsideRefServiceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 变量外部服务详情出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/16
 */
@Data
@Schema(description = "变量外部服务详情出参")
public class VariableOutsideServiceDetailOutputDto implements Serializable {

    private static final long serialVersionUID = 6632539319767939471L;

    @Schema(description = "输入参数")
    private List<InputParameter> inputParameterList;

    @Schema(description = "返回数据 (外部服务响应数据)")
    private DomainDataModelTreeDto responseData;

    @Schema(description = "引入详情-仅限接口入参 viewFrom 值为 flow 时显示")
    private List<ReferenceDetailVo> referenceDetail;

    @Data
    @Schema(description = "变量外部服务详情出参-输入参数")
    public static class InputParameter implements Serializable {

        private static final long serialVersionUID = -4067935699384947914L;

        @Schema(description = "参数名")
        private String name;

        @Schema(description = "参数中文名")
        private String label;

        @Schema(description = "数据类型")
        private String type;

        @Schema(description = "数据与变量 (映射外部服务请求参数的变量路径名)")
        private String dataModelPath;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "变量空间-外部服务引入详情")
    public static class ReferenceDetailVo implements Serializable {

        private static final long serialVersionUID = -8087681834464964194L;

        @Schema(description = "接收对象名")
        private String receiverName;

        @Schema(description = "接收对象中文名")
        private String receiverLabel;

        @Schema(description = "引入人", example = "张三")
        private String refUser;

        @Schema(description = "引入时间", example = "2022-01-07 16:18:00")
        @JsonFormat(pattern = DateUtil.FORMAT_LONG)
        private Date refTime;

        @Schema(description = "实时服务使用列表")
        private List<VariableOutsideRefServiceDto> variableServiceUsage;
    }
}
