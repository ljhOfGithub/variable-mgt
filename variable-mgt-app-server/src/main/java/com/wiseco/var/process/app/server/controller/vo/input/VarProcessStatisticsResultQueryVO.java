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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "指标回溯统计结果查询 VO")
public class VarProcessStatisticsResultQueryVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "实时服务ID")
    private Long varProcessServiceId;

    @Schema(description = "变量清单ID")
    private Long varProcessManifestId;

    @Schema(description = "指标名称")
    private String indexName;

    @Schema(description = "排序： 字段_desc/asc")
    private String order;

    @Schema(description = "排序字段")
    private String sortKey;

    @Schema(description = "排序方式")
    private String sortType;

}
