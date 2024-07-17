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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author mingao
 * @since 2023/10/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "指标回溯统计基准指标数据项查询 VO")
public class BacktrackingStatisticsReferenceValueQueryVO {
    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "回溯id")
    private Long backtrackingId;

    @Schema(description = "批次号", required = true)
    private List<String> batchNumberList;

    @Schema(description = "指标名称")
    private String indexName;

    @Schema(description = "内部数据表名称")
    private String internalTableName;
}
