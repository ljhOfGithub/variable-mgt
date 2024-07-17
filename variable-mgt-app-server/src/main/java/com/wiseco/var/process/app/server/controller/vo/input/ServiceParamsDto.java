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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceParamsDto implements Serializable {
    private static final long serialVersionUID = -6120832682080437368L;

    @Schema(description = "输入参数")
    private List<ParamTreeDto> requestStructure;

    @Schema(description = "响应参数")
    private List<ParamTreeDto> responseStructure;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ParamTreeDto {

        /**
         * 变量英文名
         */
        @Schema(description = "变量英文名", example = "variable")
        private String name;

        /**
         * 变量中文名
         */
        @Schema(description = "变量中文名", example = "变量")
        private String describe;

        /**
         * 变量类型
         */
        @Schema(description = "变量类型", example = "int")
        private String type;

        /**
         * 数组标识, 0: false, 1: true
         */
        @Schema(description = "数组标识, 0: false, 1: true")
        private String isArr = "0";

        /**
         * 属性
         */
        @Schema(description = "属性")
        private List<ParamTreeDto> children;
    }
}
