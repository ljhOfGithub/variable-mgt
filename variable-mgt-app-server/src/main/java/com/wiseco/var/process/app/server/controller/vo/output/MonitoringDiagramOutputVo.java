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

import com.wiseco.var.process.app.server.enums.MonitorIndicatorEnum;
import com.wiseco.var.process.app.server.enums.ReportFormTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 监控图表输出Vo
 *
 * @author wuweikang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "监控图表输出Vo")
public class MonitoringDiagramOutputVo implements Serializable {

    private static final long serialVersionUID = -643963492283464817L;

    @Schema(description = "报表类型, LINE_CHART(折线图), AREA_CHART(面积图), HISTOGRAM(柱状图), TOP_CHART(TOP图), RING_CHART(环形图), TABLE(表格)")
    private ReportFormTypeEnum type;

    @Schema(description = "报表的title", example = "实时服务A的调用量")
    private String title;

    @NotNull(message = "监控指标不能为空")
    @Schema(description = "监控指标, CALL_VOLUME——调用量(服务报表时选择), FAILURE_RATE——失败率(服务报表时选择), MAX_RESPONSE_TIME——最大响应时间(服务报表时选择), "
            + "AVG_RESPONSE_TIME——平均响应时间(服务报表时选择), RESPONSE_CODE_RATIO——响应码占比(服务报表时选择), MISSING_RATIO——缺失率(指标报表时选择)"
            + "SPECIAL_RATIO——特殊值占比(指标报表时选择), PSI——psi(指标报表时选择), IV——iv(指标报表时选择)")
    private MonitorIndicatorEnum monitorIndicator;

    /**
     * 图
     */
    @Schema(description = "图")
    private MonitoringDiagram monitoringDiagram;

    /**
     * 表
     */
    @Schema(description = "表")
    private MonitoringTable monitoringTable;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "图")
    public static class MonitoringDiagram {

        @Schema(description = "x轴", example = "")
        private List<String> xAxis;


        @Schema(description = "y轴", example = "")
        private List<String> yAxis;


        @Schema(description = "数据项", example = "")
        private List<Series> dataList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Series")
    public static class Series {

        @Schema(description = "name,不为环形图时存在")
        private String name;

        @Schema(description = "图类型")
        private String type;

        @Schema(description = "数据", example = "")
        private List<Value> data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Value")
    public static class Value {
        @Schema(description = "name,环形图时存在")
        private String name;

        @Schema(description = "值")
        private String value;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "表")
    public static class MonitoringTable {
        /**
         * headers
         */
        @Schema(description = "headers", example = "")
        private List<Header> headers;
        /**
         * datas
         */
        @Schema(description = "dataList", example = "")
        private List<Map<String, String>> datas;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "列表Header")
    public static class Header {
        /**
         * label
         */
        @Schema(description = "label", example = "")
        private String label;

        /**
         * value
         */
        @Schema(description = "value", example = "")
        private String value;
    }

}
