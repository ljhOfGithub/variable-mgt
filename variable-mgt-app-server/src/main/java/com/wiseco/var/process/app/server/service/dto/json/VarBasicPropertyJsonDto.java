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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author liukl
 */

@Schema(description = "变量路径一级基本属性JSON")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VarBasicPropertyJsonDto implements Serializable {

    private static final long serialVersionUID = 4532543619323997379L;

    private String type;

    private List<Line> lines;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Line {

        private List<Part> parts;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Part {
        private String type;

        private String label;

        private String name;

        private String value;

        private String isArr;

        private String dataType;

        private String typeRef;

        private String marginLeft;

    }

}
