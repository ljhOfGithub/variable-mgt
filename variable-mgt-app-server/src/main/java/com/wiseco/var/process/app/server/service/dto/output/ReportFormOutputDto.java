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
package com.wiseco.var.process.app.server.service.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.enums.MonitorIndicatorEnum;
import com.wiseco.var.process.app.server.enums.ReportFormCategoryEnum;
import com.wiseco.var.process.app.server.enums.ReportFormTypeEnum;
import com.wiseco.var.process.app.server.enums.ReportFromStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 报表的输出Dto(业务逻辑层)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "报表的输出Dto(业务逻辑层)")
public class ReportFormOutputDto implements Serializable {

    private static final long serialVersionUID = 6425122894057022133L;

    @Schema(description = "报表的ID", example = "1")
    private Long id;

    @Schema(description = "报表的名称", example = "服务报表")
    private String name;

    @Schema(description = "报表的分类, SERVICE——服务报表, VARIABLE——指标报表", example = "SERVICE")
    private ReportFormCategoryEnum category;

    @Schema(description = "报表类型, LINE_CHART(折线图), AREA_CHART(面积图), HISTOGRAM(柱状图), TOP_CHART(TOP图), RING_CHART(环形图), TABLE(表格)", example = "LINE_CHART")
    private ReportFormTypeEnum type;

    @Schema(description = "监控指标, CALL_NUM——调用量(服务报表时展示), FAILURE_RATIO——失败率(服务报表时展示), MAX_RESPONSE_TIME——最大响应时间(服务报表时展示), "
            + "AVERAGE_RESPONSE_TIME——平均响应时间(服务报表时展示), RESPONSE_CODE_RATIO——响应码占比(服务报表时展示), MISSING_RATIO——缺失率(指标报表时展示)"
            + "SPECIAL_RATIO——特殊值占比(指标报表时展示), PSI——psi(指标报表时展示), IV——iv(指标报表时展示)", example = "CALL_NUM")
    private MonitorIndicatorEnum indicator;

    @Schema(description = "状态, UP——启用, DOWN——停用, EDIT——编辑中", example = "UP")
    private ReportFromStateEnum state;

    @Schema(description = "创建人", example = "SSO管理员")
    private String createdUser;

    @Schema(description = "创建时间", example = "2023-11-11 05:25:27")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @Schema(description = "最后编辑人", example = "SSO管理员")
    private String updatedUser;

    @Schema(description = "最后编辑时间", example = "2023-11-17 05:25:27")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;
}
