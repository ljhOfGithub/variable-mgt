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
 * @since: 2021/12/2
 */
@Data
@Builder
@Schema(description = "获取变量中的基础类型数组数据 入参DTO")
@NoArgsConstructor
@AllArgsConstructor
public class TreeVarBaseArrayInputDto {

    @Schema(description = "1(或空)策略组件，2变量加工", required = true, example = "1")
    private Integer type;

    @Schema(description = "组件首次生成时的策略ID", required = true, example = "1")
    @NotNull(message = "策略不能为空")
    private Long strategyId;

    @Schema(description = "空间ID", required = true, example = "1")
    @NotNull(message = "空间不能为空")
    private Long spaceId;

    @Schema(description = "位置入参", required = true, example = "['input','output','engineVars','externalData','commonData']")
    @NotNull(message = "位置入参不能为空")
    private List<String> positionList;

    @Schema(description = "类型入参", example = "['int','double','boolean','object']")
    private List<String> typeList;

    //    @Schema(description = "全部位置的jsonschema", example = "['input','output','engineVars','externalData','commonData']")
    //    private Map<String, String> jsonschemaMap;

    @Schema(description = "全部位置的jsonschema对应的DomainDataModelTreeDto", example = "['input','output','engineVars','externalData','commonData']")
    private Map<String, DomainDataModelTreeDto> jsonschemaDtoMap;
}
