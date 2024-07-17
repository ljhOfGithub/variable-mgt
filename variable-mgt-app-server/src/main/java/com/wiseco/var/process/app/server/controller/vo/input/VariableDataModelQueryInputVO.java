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

import com.wiseco.boot.data.PageDTO;
import com.wiseco.var.process.app.server.commons.enums.SortType;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量数据模型查询 DTO")
public class VariableDataModelQueryInputVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "对象名或中文名", example = "征信")
    private String name;

    @Schema(description = "对象来源")
    private VarProcessDataModelSourceType sourceType;

    @Schema(description = "是否使用")
    private Boolean used;

    @Schema(description = "创建部门")
    private String createdDept;

    @Schema(description = "排序：字段名称")
    private String sortKey;

    @Schema(description = "升序/降序")
    private SortType sortType;

    @Schema(description = "部门code列表")
    private List<String> deptCodes;

    @Schema(description = "用户名列表")
    private List<String> userNames;
}
