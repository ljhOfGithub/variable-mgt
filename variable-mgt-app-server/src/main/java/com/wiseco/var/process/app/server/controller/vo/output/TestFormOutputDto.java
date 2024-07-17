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

import com.wiseco.var.process.app.server.commons.test.dto.TestFormPathOutputDto;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: wangxianli
 */
@Data
@Schema(description = "表单结构 出参Dto")
public class TestFormOutputDto {

    @Schema(description = "输入变量", example = "null")
    private List<TestFormPathOutputDto> input;

    /*@Schema(description = "输出结果", example = "null")
    private List<TestFormPathOutputDto> output;*/

    @Schema(description = "预期结果变量", example = "null")
    private List<TestFormPathOutputDto> expect;

    @Schema(description = "预期结果选中表头", example = "null")
    private List<String> expectHeader;

    @Schema(description = "输入数据", example = "null")
    private JSONObject inputValue;

    @Schema(description = "预期数据", example = "null")
    private JSONObject expectValue;

}
