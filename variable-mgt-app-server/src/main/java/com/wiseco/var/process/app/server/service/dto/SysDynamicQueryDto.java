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
import java.util.List;

/**
 * @author wangxianli
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "动态查询DTO")
public class SysDynamicQueryDto implements Serializable {

    @Schema(description = "空间类型:domain-领域空间，external-外数空间", example = "domain")
    private String spaceType;

    @Schema(description = "空间类型对应的业务ID", example = "1")
    private Long spaceBusinessId;

    /**
     * 策略ID
     */
    @Schema(description = "策略ID", example = "1")
    private Long strategyId;

    @Schema(description = "角色ID", example = "null")
    private List<Long> roleIds;

    @Schema(description = "开始时间", example = "null")
    private String startTime;

    @Schema(description = "最多查询动态数量", example = "null")
    private Integer dynamicCount;

}
