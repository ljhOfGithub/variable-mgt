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

import com.wiseco.var.process.app.server.commons.DataBasePage;
import com.wiseco.var.process.app.server.commons.HeaderBasePage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量数据查询 VO")
public class BacktrackingDatasetQueryVO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "批量回溯任务id", required = true)
    private Integer backtrackingId;

    @Schema(description = "任务批次号", example = "默认为null，用户点击下拉列表选择, 可选择多个", required = false)
    private List<String> backtrackingTaskCodes;

    @Schema(description = "列分页", required = true)
    private HeaderBasePage columnPageInfo;

    @Schema(description = "行分页", required = true)
    private DataBasePage rowPageInfo;

    @Schema(description = "排序字段", example = "label_asc", required = false)
    private String order;

    @Schema(description = "条件", required = false)
    private List<FieldConditionVO> fieldConditions;

    @Schema(description = "显示列，显示列没设置传null", required = false, example = "null")
    private List<String> columns;

}
