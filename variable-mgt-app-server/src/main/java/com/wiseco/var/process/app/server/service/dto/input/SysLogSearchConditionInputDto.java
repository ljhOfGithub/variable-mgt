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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 系统日志搜索条件入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/5/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统日志搜索条件入参 DTO")
public class SysLogSearchConditionInputDto extends PageDTO {

    @Schema(description = "起始时间", example = "2022-05-25 00:00:00")
    private String timeSpanStart;

    @Schema(description = "截止时间", example = "2022-05-25 23:59:59")
    private String timeSpanEnd;

    @Schema(description = "用户名 (模糊搜索用)")
    private String userName;

    @Schema(description = "日志描述关键词 (模糊搜索用)")
    private String keyword;
}
