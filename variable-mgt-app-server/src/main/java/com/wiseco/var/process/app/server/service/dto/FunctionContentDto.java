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
public class FunctionContentDto {

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

        @JsonProperty("result_bindings")
        private Object resultBindings;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class BaseData implements Serializable {

        @JsonProperty("data_model")
        private DataModel dataModel;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class DataModel implements Serializable {

        private List<LocalVar> parameters;
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

        private String dictCode;

    }
}
