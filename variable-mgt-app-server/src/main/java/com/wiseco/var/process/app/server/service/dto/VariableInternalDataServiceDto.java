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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 内部数据关联服务接口列表
 *
 * @author wangxianli
 * @since 2022/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内部数据关联服务接口列表 DTO")
public class VariableInternalDataServiceDto implements Serializable {

    @Schema(description = "identifier", example = "")
    private String identifier;

    @Schema(description = "服务名称", example = "")
    private String name;

    @Schema(description = "服务版本", example = "")
    private String version;

    @Schema(description = "发布变量数", example = "")
    private Integer varNums;

    @Schema(description = "服务状态：0: 编辑中, 1: 测试中, 2: 待审核, 3: 审核拒绝, 4: 启用，5：停用，6：发布失败", example = "1")
    private Integer state;

}
