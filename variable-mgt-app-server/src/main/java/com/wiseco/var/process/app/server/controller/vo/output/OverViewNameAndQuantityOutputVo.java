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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@ApiModel(value = "睿信概览统计数量与状态")
public class OverViewNameAndQuantityOutputVo implements Serializable {
    private static final long SERIAL_VERSION_UID = 8759865846955173993L;

    @ApiModelProperty(value = "实时服务id")
    private Integer serviceId;

    @ApiModelProperty(value = "实时服务名称")
    private String serviceName;

    @ApiModelProperty(value = "实时服务调用次数")
    private Long serviceCallQuantity;

    @ApiModelProperty(value = "实时服务平均响应时间")
    private BigDecimal serviceResponseLongTime;

    @ApiModelProperty(value = "变量清单id")
    private Integer manifestId;

    @ApiModelProperty(value = "变量清单名称")
    private String manifestName;

    @ApiModelProperty(value = "变量清单中的变量数")
    private Integer manifestVarQuantity;


}
