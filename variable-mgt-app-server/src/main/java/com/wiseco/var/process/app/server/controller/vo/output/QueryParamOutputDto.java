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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: liusiyu
 * @date: 2024/1/17
 * @Time: 19:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查询参数出参DTO")
public class QueryParamOutputDto {

    @Schema(description = "所属分类", example = "builtInParam")
    private String category;

    @Schema(description = "变量英文名", example = "name")
    private String varName;

    @Schema(description = "变量全路径", example = "builtInParam.name")
    private String varFullPath;

    @Schema(description = "查询条件对应中文名", example = "用户姓名")
    private String label;

    @Schema(description = "数据类型", example = "string")
    private String type;

    @Schema(description = "排序权重")
    private Integer weight;

    @Schema(description = "下拉选择类型的选择项")
    private List<Map<String, Object>> selectList;

}
