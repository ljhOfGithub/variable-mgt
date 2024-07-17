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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 监控报表的删除入参Vo
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "监控报表的删除入参Vo")
public class ReportFormDeleteInputVo implements Serializable {

    private static final long serialVersionUID = 6609177352692929602L;

    @NotNull(message = "监控报表的Id不能为空")
    @Schema(description = "监控报表的Id", example = "10001")
    private Long id;
}
