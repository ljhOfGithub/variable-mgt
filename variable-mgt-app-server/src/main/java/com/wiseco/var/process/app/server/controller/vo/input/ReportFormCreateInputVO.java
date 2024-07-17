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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wiseco.var.process.app.server.controller.vo.DisplayDimensionVo;
import com.wiseco.var.process.app.server.controller.vo.IndicatorMappingVo;
import com.wiseco.var.process.app.server.controller.vo.MonitorObjectMappingVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.enums.ReportFormCategoryEnum;
import com.wiseco.var.process.app.server.enums.ReportFormTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 监控预警中报表创建的入参VO(Controller层)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "监控预警中报表创建的入参VO(Controller层)")
public class ReportFormCreateInputVO implements Serializable {

    private static final long serialVersionUID = -3711255372057336758L;

    @Schema(description = "报表的Id,当修改已有的报表时赋值,如果是新增就不赋值")
    private Long id;

    @NotBlank(message = "报表名称不能为空")
    @Size(max = 100, message = "报表名称不能超过 100 个字符")
    @Schema(description = "报表名称")
    private String name;

    @NotNull(message = "报表分类不能为空")
    @Schema(description = "报表分类, SERVICE——服务报表, SINGLE_VARIABLE_ANALYZE——单指标分析报表, VARIABLE_COMPARE_ANALYZE——指标对比分析报表", example = "SERVICE")
    private ReportFormCategoryEnum categoryEnum;

    @NotNull(message = "时间范围的开始时间不能为空")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "时间范围的开始时间", example = "2022-10-15 10:20:30")
    private LocalDateTime startTime;

    @NotNull(message = "时间范围的结束时间不能为空")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "时间范围的结束时间", example = "2023-10-15 10:20:30")
    private LocalDateTime endTime;

    @NotNull(message = "监控对象不能为空")
    @Schema(description = "监控对象")
    private MonitorObjectMappingVo monitorObjectMappingVo;

    @Schema(description = "变量清单, 当选择了单指标分析报表和指标对比分析报表时赋值")
    private List<ServiceManifestNameVo> manifests;

    @NotNull(message = "监控指标不能为空")
    @Valid
    @Schema(description = "监控指标(对象形式)")
    private IndicatorMappingVo indicatorMappingVo;

    @NotNull(message = "展示维度不能为空")
    @Valid
    @Schema(description = "展示维度(对象形式)")
    private DisplayDimensionVo displayDimensionVo;

    @NotNull(message = "报表类型不能为空")
    @Schema(description = "报表类型, LINE_CHART(折线图), AREA_CHART(面积图), HISTOGRAM(柱状图), TOP_CHART(TOP图), RING_CHART(环形图), TABLE(表格)")
    private ReportFormTypeEnum type;

    @Schema(description = "报表排序(可填可不填, 如果填, 写入integer型数据)", example = "10")
    private Integer order;
}