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
import java.util.List;
import java.util.Map;

/**
 * 变量结果列表查询入参 DTO
 * <p>适用于变量结果列表查询</p>
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "变量结果列表查询输入参数")
public class VariableResultListQueryInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = -2071597727619749745L;

    @Schema(description = "内置参数")
    private BuiltInParam builtInParam;

    @Schema(description = "自定义参数")
    private CustomParam customParam;

    @Data
    public static class BuiltInParam implements Serializable {

        @Schema(description = "调用方")
        private String caller;

        @Schema(description = "查询状态", example = "失败, 成功")
        private String resultStatus;

        @Schema(description = "实时服务名称")
        private String serviceName;

        @Schema(description = "实时服务版本id")
        private Long serviceVersion;

        @Schema(description = "清单类型, 1——主清单, 0——异步清单", example = "1")
        private Integer manifestType;

        @Schema(description = "清单id")
        private Long manifestId;

        @Schema(description = "内部流水号")
        private String engineSerialNo;

        @Schema(description = "主体唯一标识")
        private String principalUniqueIdentification;

        @Schema(description = "调用时间")
        private List<String> requestTime;

        @Schema(description = "响应时长")
        private List<Long> responseTime;

        @Schema(description = "实时服务(具体的)ID集合,数据权限相关的")
        private List<Long> serviceIds;

        @Schema(description = "排序字段")
        private String order;
    }

    @Data
    public static class CustomParam implements Serializable {
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

        @Schema(description = "变量")
        private Map<String, Object> var;
    }
}
