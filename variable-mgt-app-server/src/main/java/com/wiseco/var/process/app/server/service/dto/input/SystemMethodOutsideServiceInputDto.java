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

/**
 *
 * @author: xiewu
 * @since: 2021/12/24
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "外部服务系统方法 入参DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SystemMethodOutsideServiceInputDto extends PageDTO {

    @Schema(description = "查询条件", example = "")
    private String queryNameOrNameCn;

}
