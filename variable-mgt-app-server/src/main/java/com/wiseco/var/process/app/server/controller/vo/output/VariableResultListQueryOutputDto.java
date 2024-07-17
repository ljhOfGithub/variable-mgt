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
import java.util.Map;

/**
 * 变量结果列表查询出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量结果列表查询输出参数")
public class VariableResultListQueryOutputDto implements Serializable {

    private static final long serialVersionUID = 6465698601616893560L;

    @Schema(description = "内部流水号")
    private String engineSerialNo;

    @Schema(description = "主体唯一标识")
    private String principalUniqueIdentification;

    @Schema(description = "决策流水号")
    private String restSerialNo;

    @Schema(description = "调用时间 (开始)")
    private String requestTime;

    @Schema(description = "实时服务名称")
    private String serviceName;

    @Schema(description = "实时服务版本")
    private String serviceVersion;

    @Schema(description = "调用清单")
    private String manifestName;

    @Schema(description = "调用方")
    private String caller;

    @Schema(description = "查询状态", example = "失败, 成功")
    private String resultStatus;

    @Schema(description = "实时服务id")
    private Long serviceId;

    @Schema(description = "响应时长")
    private Long responseTime;

    @Schema(description = "排序字段")
    private String order;

    @Schema(description = "客户编号")
    private String custNo;

    @Schema(description = "姓名")
    private String custName;

    @Schema(description = "证件类型")
    private String certType;

    @Schema(description = "证件号码")
    private String certNo;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "渠道编码")
    private String channelCode;

    @Schema(description = "业务场景")
    private String bizType;

    @Schema(description = "开启trace")
    private Boolean enableTrace;

    @Schema(description = "变量")
    private Map<String,Object> vars;
}
