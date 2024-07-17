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

import com.wiseco.boot.data.PageDTO;
import com.wiseco.var.process.app.server.enums.ReportFormCategoryEnum;
import com.wiseco.var.process.app.server.enums.ReportFromStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 报表管理的搜索入参
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "报表管理的搜索入参(业务逻辑层)")
public class ReportFormSearchInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 7535219373101531054L;

    @Schema(description = "报表分类, 可以为空, 也可以是SERVICE——服务报表, SINGLE_VARIABLE_ANALYZE——单指标分析报表, VARIABLE_COMPARE_ANALYZE——指标对比分析报表", example = "SERVICE")
    private ReportFormCategoryEnum category;

    @Schema(description = "状态, 可以为空, 也可以是UP——启用, DOWN——停用, EDIT——编辑中", example = "EDIT")
    private ReportFromStateEnum state;

    @Schema(description = "报表的名称, 可以为空", example = "123")
    private String name;
}
