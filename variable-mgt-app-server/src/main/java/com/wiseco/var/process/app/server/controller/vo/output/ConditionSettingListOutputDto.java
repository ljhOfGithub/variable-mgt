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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: liusiyu
 * @date: 2024/2/27
 * @Time: 13:56
 */
@Data
@Schema(description = "查询条件，表头列查询 出参DTO")
public class ConditionSettingListOutputDto {

    @Schema(description = "内置参数")
    private List<ConditionSettingOutputDto> builtInHead;

    @Schema(description = "自定义参数")
    private List<ConditionSettingOutputDto> customHead;

    @Schema(description = "查询参数列表")
    private List<QueryParamOutputDto> queryParamList;

    @Schema(description = "清单变量中作为搜索条件的变量")
    private List<QueryParamOutputDto> searchableVariableList;

}
