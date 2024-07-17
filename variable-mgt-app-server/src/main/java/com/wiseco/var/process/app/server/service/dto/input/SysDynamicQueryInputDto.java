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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxianli
 * @since 2022/3/2
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "动态信息查询DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SysDynamicQueryInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 8275238326146990604L;

    @Schema(description = "空间类型:domain-领域空间，external-外数空间，variable-变量空间", example = "domain")
    private String spaceType;

    @Schema(description = "空间类型对应的业务ID", example = "1")
    private Long spaceBusinessId;

    /**
     * 策略ID
     */
    @Schema(description = "策略ID", example = "1")
    private Long strategyId;

}
