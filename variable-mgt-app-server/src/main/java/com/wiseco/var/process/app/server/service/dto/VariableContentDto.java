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
package com.wiseco.var.process.app.server.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wisecotech.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangxianli
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VariableContentDto {

    @JsonProperty("specific_data")
    private SpecificData specificData;

    @JsonProperty("base_data")
    private BaseData baseData;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SpecificData implements Serializable {
        /**
         * body : {}
         */
        private JSONObject body;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class BaseData implements Serializable {

        /**
         * specialValue : [{"name":"bbb","index":0,"label":"bbb"}]
         */
        private List<EnumValueEntity> specialValue;

        /**
         * dataRange : {"opr":"小于等于..小于","vals":"[18,25]"}
         */
        private DataRange dataRange;

        /**
         * enumValue : [{"name":"bbb","index":0,"label":"bbb"}]
         */
        private List<EnumValueEntity> enumValue;

        /**
         * dataType : string
         */
        private String dataType;

        /**
         * precision : 精度
         */
        private String precision;

        /**
         * name : 变量名
         */
        private String name;

        /**
         * label : 变量中文名
         */
        private String label;

        /**
         * localVars : [{"name":"aaa","index":0,"isArray":false,"label":"aaa","type":"string"}]
         */
        @JsonProperty("data_model")
        private DataModel dataModel;

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class EnumValueEntity implements Serializable {
            /**
             * name : bbb
             * index : 0
             * label : bbb
             */
            private String name;
            private int index;
            private String label;

        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class DataRange implements Serializable {
            /**
             * opr : 小于等于..小于
             * vals : [18,25]
             */
            private String opr;
            private List<String> vals;

        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class DataModel implements Serializable {

        private List<LocalVar> localVars;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LocalVar implements Serializable {

        private int index;

        private String name;

        private String type;

        private String label;

        private Boolean isArray;

    }
}
