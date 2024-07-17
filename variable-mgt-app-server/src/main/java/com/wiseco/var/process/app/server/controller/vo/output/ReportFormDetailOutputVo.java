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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wiseco.var.process.app.server.controller.vo.DisplayDimensionVo;
import com.wiseco.var.process.app.server.controller.vo.IndicatorMappingVo;
import com.wiseco.var.process.app.server.controller.vo.MonitorObjectMappingVo;
import com.wiseco.var.process.app.server.enums.ReportFormCategoryEnum;
import com.wiseco.var.process.app.server.enums.ReportFormTypeEnum;
import com.wiseco.var.process.app.server.enums.ReportFromStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 监控报表的详情(控制层)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "监控报表的详情(控制层)")
public class ReportFormDetailOutputVo implements Serializable {

    private static final long serialVersionUID = 7374612489838591381L;

    @Schema(description = "监控报表的Id", example = "10001")
    private Long id;

    @Schema(description = "监控报表的名称", example = "监控实时服务A的调用量")
    private String name;

    @Schema(description = "监控报表的分类, SERVICE——服务报表, SINGLE_VARIABLE_ANALYZE——单指标分析报表, VARIABLE_COMPARE_ANALYZE——指标对比分析报表", example = "SERVICE")
    private ReportFormCategoryEnum category;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "时间范围的开始时间", example = "2022-10-15 10:20:30")
    private LocalDateTime startTime;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "时间范围的结束时间", example = "2023-10-15 10:20:30")
    private LocalDateTime endTime;

    @Schema(description = "监控对象")
    private MonitorObjectMappingVo monitorObjectMappingVo;

    @Schema(description = "变量清单, 当选择了单指标分析报表和指标对比分析报表时赋值")
    private List<ServiceManifestNameVo> manifests;

    @Schema(description = "监控指标(对象形式)")
    private IndicatorMappingVo indicatorMappingVo;

    @Schema(description = "展示维度(对象形式)")
    private DisplayDimensionVo displayDimensionVo;

    @Schema(description = "报表类型, LINE_CHART(折线图), AREA_CHART(面积图), HISTOGRAM(柱状图), TOP_CHART(TOP图), RING_CHART(环形图), TABLE(表格)")
    private ReportFormTypeEnum type;

    @Schema(description = "报表排序(可填可不填, 如果填, 写入integer型数据)", example = "10")
    private Integer order;

    @Schema(description = "监控报表的状态, UP——启用, DOWN——停用, EDIT——编辑中", example = "EDIT")
    private ReportFromStateEnum state;
}
