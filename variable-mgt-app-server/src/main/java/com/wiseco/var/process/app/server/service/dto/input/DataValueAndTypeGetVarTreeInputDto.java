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
package com.wiseco.var.process.app.server.service.dto.input;

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 *
 * @author: xiewu
 * @since: 2021/12/9
 */
@Schema(description = "根据路径和类型获取动态变量树 入参DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DataValueAndTypeGetVarTreeInputDto {

    @Schema(description = "类型 1:策略组件 2:空间变量", example = "1")
    private Integer type;

    @Schema(description = "全局变量id,策略id或空间id", required = true, example = "1")
    @NotNull(message = "策略不能为空")
    private Long globalId;

    @Schema(description = "数据查找节点", required = true, example = "input.aa.dd")
    @NotNull(message = "数据查找节点")
    private String dataValue;

    @Schema(description = "类型入参", example = "['int','double','boolean']")
    private List<String> typeList;

    //    @Schema(description = "全部位置的jsonschema", example = "['input','output','engineVars','externalData','commonData']")
    //    private Map<String, String> jsonschemaMap;

    @Schema(description = "全部位置的jsonschema对应的DomainDataModelTreeDto", example = "['input','output','engineVars','externalData','commonData']")
    private Map<String, DomainDataModelTreeDto> jsonschemaDtoMap;
}
