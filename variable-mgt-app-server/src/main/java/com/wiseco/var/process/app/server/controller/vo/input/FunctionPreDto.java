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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author wiseco
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "数据预处理直接映射 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FunctionPreDto extends DomainDataModelTreeDto {

    private static final long serialVersionUID = 8154565436953440787L;

    @Schema(description = "选择参数")
    private String param;

    @Schema(description = "参数值映射")
    private Map<String, String> valueMap;

}
