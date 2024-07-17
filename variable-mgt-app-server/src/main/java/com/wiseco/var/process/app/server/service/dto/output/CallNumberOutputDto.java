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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "调用量报表")
public class CallNumberOutputDto implements Serializable {

    /**
     * 调用量报表列表
     */
    @Schema(description = "调用量报表列表", example = "")
    private CallNumberReport callNumberReport;

    /**
     * 调用量柱状图折线图
     */
    @Schema(description = "调用量柱状图折线图", example = "")
    private CallNumberDiagram callNumberDiagram;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "调用量报表列表")
    public static class CallNumberReport {

        /**
         * headers
         */
        @Schema(description = "headers", example = "")
        private List<Header> headers;
        /**
         * datas
         */
        @Schema(description = "datas", example = "")
        private List<Map<String, String>> datas;

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

            /**
             * fixed
             */
            @Schema(description = "fixed", example = "")
            private String fixed;

        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "调用量柱状图折线图")
    public static class CallNumberDiagram {

        /**
         * x轴
         */
        @Schema(description = "x轴", example = "")
        @JsonProperty("xAxis")
        private List<Xaxis> xAxis;

        /**
         * x轴
         */
        @Schema(description = "y轴", example = "")
        @JsonProperty("yAxis")
        private List<Yaxis> yAxis;

        /**
         * series
         */
        @Schema(description = "调用量柱状图折线图", example = "")
        @JsonProperty("series")
        private List<Series> series;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Schema(description = "图XAxis")
        public static class Xaxis {

            /**
             * type
             */
            @Schema(description = "label", example = "")
            private String type = "category";

            /**
             * data
             */
            @Schema(description = "value", example = "")
            private List<Value> data;

        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Schema(description = "图YAxis")
        public static class Yaxis {

            /**
             * type
             */
            private String type;

        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Schema(description = "图Series")
        public static class Series {

            /**
             * name
             */
            @Schema(description = "name", example = "")
            private String name;

            /**
             * type
             */
            @Schema(description = "type", example = "")
            private String type;

            /**
             * data
             */
            @Schema(description = "value", example = "")
            private List<Value> data;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Schema(description = "Value")
        public static class Value {

            /**
             * value
             */
            @Schema(description = "value", example = "")
            private String value;

        }

    }
}
