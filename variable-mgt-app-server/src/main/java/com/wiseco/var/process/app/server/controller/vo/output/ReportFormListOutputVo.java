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

import com.wiseco.var.process.app.server.service.dto.output.ReportFormOutputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 返回给控制层的报表信息实体类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "返回给控制层的报表信息实体类")
public class ReportFormListOutputVo implements Serializable {

    private static final long serialVersionUID = -571316522374205159L;

    @Schema(description = "报表信息列表", example = "list")
    private List<ReportFormOutputDto> records;

    @Schema(description = "总行数", example = "99")
    private Long total;
}
