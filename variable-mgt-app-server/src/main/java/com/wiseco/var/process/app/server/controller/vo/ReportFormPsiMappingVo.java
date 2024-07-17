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
package com.wiseco.var.process.app.server.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.enums.ReportFormPsiEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 监控报表的PSI实体类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "监控报表的PSI实体类")
public class ReportFormPsiMappingVo implements Serializable {

    private static final long serialVersionUID = -5741603578031162595L;

    @Schema(description = "PSI的枚举, DATETIME_SCOPE_DATA: 选择时间范围数据作为基准, BASIC_INDICATOR: 选择基准指标, MANIFEST: 选择清单")
    private ReportFormPsiEnum baseIndexFlag;

    @Schema(description = "开始时间, 报表分类为单指标分析和指标对比分析时可以选择", example = "1970-01-01 00:00:00")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startDateTime;

    @Schema(description = "结束时间, 报表分类为单指标分析和指标对比分析时可以选择", example = "2000-01-01 00:00:00")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endDateTime;

    @Schema(description = "基准分组指标, 报表分类为单指标分析和指标对比分析时可以选择", example = "指标名称")
    private String baseIndex;

    @Schema(description = "基准数据项, 报表分类为单指标分析和指标对比分析时可以选择")
    private String baseIndexVal;

    @Schema(description = "基准指标调用时间段, 报表分类为单指标分析和指标对比分析时可以选择, true——与设置的时间维度一致, false——所有时间段", example = "true")
    private Boolean baseIndexCallDate;

    @Schema(description = "基准清单, 报表分类为单指标分析时可以选择")
    private ServiceManifestNameVo serviceManifestNameVo;
}
