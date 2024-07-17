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
package com.wiseco.var.process.app.server.service.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author: liaody
 * @since: 2021/10/21
 */
@Schema(description = "组件JsonDTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ComponentJsonDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "base_data")
    @JsonProperty("base_data")
    private BaseData baseData;

    @Schema(description = "specific_data")
    @JsonProperty("specific_data")
    private SpecificData specificData;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class BaseData implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "data_model")
        @JsonProperty("data_model")
        private DataModel dataModel;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class DataModel implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "parameters")
        private List<Parameter> parameters;

        @Schema(description = "localVars")
        private List<LocalVar> localVars;
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Parameter extends Field implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "direction")
        private String direction;
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @Data
    @Builder
    public static class LocalVar extends Field implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Field implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "index")
        private int index;

        @Schema(description = "name")
        private String name;

        //自定义类型是 input.application,引用java类型是
        @Schema(description = "type")
        private String type;

        @Schema(description = "label")
        private String label;

        @Schema(description = "isArray")
        private Boolean isArray;

        //自定义类型是 input.application,引用java类型是
        @Schema(description = "type")
        private String javaType;

        //自定义类型是 input.application,引用java类型是
        @Schema(description = "type")
        private String objectType;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class DataFieldAccess implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "name")
        private String name;

        @Schema(description = "access_type")
        @JsonProperty("access_type")
        private String accessType;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class SpecificData implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "nodes")
        private List<JSONObject> nodes;

        @Schema(description = "rules")
        private List<Rule> rules;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Node implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "node_id")
        @JsonProperty("node_id")
        private String nodeId;

        @Schema(description = "引用已经创建实例的决策组件:task, service, split|parallel, abtest", example = "task")
        private String type;

        @Schema(description = "label")
        private String label;

        @Schema(description = "备注")
        private String annotation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Rule implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "header")
        private Header header;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Header implements Serializable {
        private static final long serialVersionUID = 181679563138063340L;

        @Schema(description = "identifier")
        private String identifier;

        @Schema(description = "name")
        private String name;

        @Schema(description = "rule_code")
        @JsonProperty("rule_code")
        private String ruleCode;

        @Schema(description = "rule_state")
        @JsonProperty("rule_state")
        private Integer ruleState;

        @Schema(description = "description")
        private String description;

        @Schema(description = "create_time")
        @JsonProperty("create_time")
        private String createTime;
    }
}
