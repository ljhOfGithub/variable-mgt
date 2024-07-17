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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: wangxianli
 */
@Schema(description = "下载Excel模板DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestTemplateOutputDto {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "输入数据", required = true, example = "1")
    private List<DomainDataModelTreeDto> input;

    @Schema(description = "输出结果", example = "null")
    private List<DomainDataModelTreeDto> output;

}
