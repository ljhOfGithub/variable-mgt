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

import com.wiseco.var.process.app.server.commons.DataBasePage;
import com.wiseco.var.process.app.server.commons.HeaderBasePage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author mingao
 * @since 2023/9/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量数据分页 VO")
public class BacktrackingMainifestResultVO implements Serializable {

    @Schema(description = "变量数据记录")
    private List<Map<String, Object>> dataRecords;

    @Schema(description = "列数据记录")
    private List<String> columnRecords;

    @Schema(description = "列分页信息")
    private HeaderBasePage columnPageInfo;

    @Schema(description = "行分页信息")
    private DataBasePage rowPageInfo;
}
