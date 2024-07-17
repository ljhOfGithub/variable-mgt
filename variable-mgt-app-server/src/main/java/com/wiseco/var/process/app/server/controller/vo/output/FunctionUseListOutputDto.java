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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: wangxianli
 */
@Schema(description = "公共函数使用列表DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FunctionUseListOutputDto {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "标题", required = true, example = "1")
    private String title;

    @Schema(description = "表头", example = "null")
    private List<HeaderDto> header;

    @Schema(description = "内容", example = "null")
    private List<JSONObject> content;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeaderDto {

        @Schema(description = "表头描述信息", required = true, example = "1")
        private String label;

        @Schema(description = "表头code", required = true, example = "1")
        private String code;
    }

}
