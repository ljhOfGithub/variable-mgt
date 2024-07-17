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

/**
 * 外数服务调用查看列表
 *
 * @author wiseco
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "外数服务调用查看列表 VO")
public class BacktrackingOutsideVO implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "内部流水号")
    private String outsideServiceSerialNo;

    @Schema(description = "调用时间")
    private String reuqestStartDate;

    @Schema(description = "主题唯一标识???")
    private String businessSerialNo;

    @Schema(description = "外部调用流水号")
    private String decisionSerialNo;

    @Schema(description = "查询状态")
    private Integer callSuccess;

    @Schema(description = "是否Mock")
    private Integer mockData;

    @Schema(description = "是否查得")
    private Integer businessSuccess;

    @Schema(description = "命中缓存")
    private Integer hitCache;

    @Schema(description = "响应时长")
    private Integer costMillisecond;

    @Schema(description = "重试次数")
    private Integer tryTimes;

}
