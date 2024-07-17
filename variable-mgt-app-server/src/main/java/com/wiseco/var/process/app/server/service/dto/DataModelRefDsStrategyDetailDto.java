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

/**
 * 公共决策模块路由表记录 DTO
 *
 * @author XieWu
 * @since 2022/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查询数据模型策略引用信息 DTO")
public class DataModelRefDsStrategyDetailDto {

    @Schema(description = "领域id", example = "")
    private Long domainId;

    @Schema(description = "数据模型id", example = "")
    private Long dataModelId;

    @Schema(description = "服务名称", example = "")
    private String serviceName;

    @Schema(description = "细分名称", example = "")
    private String bucketName;

    @Schema(description = "策略名称", example = "")
    private String strategyName;

    @Schema(description = "状态 1开发 2测试 3待审批 4审批拒绝 5生产 6灰度 7回退 8已下线 9已删除 10陪跑 11等待上线", example = "")
    private Integer status;

    @Schema(description = "版本号", example = "")
    private String version;

}
