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
package com.wiseco.var.process.app.server.service.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: xiewu
 * @since: 2022/3/29 11:17
 */
@Schema(description = "评分卡结果集合DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ServiceCallRecordDomainNameOutputDto {

    @Schema(description = "领域主键", example = "")
    private Long id;

    @Schema(description = "领域编码", example = "")
    private String code;

    @Schema(description = "领域名称", example = "")
    private String name;
}
