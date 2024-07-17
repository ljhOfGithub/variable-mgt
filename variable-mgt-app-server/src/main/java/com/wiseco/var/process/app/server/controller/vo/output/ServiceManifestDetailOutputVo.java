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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "服务中选择的变量清单的详情Vo")
public class ServiceManifestDetailOutputVo implements Serializable {
    private static final long SERIA_VERSIONUID = 8799865846955173992L;

    @Schema(description = "清单id")
    private Long manifestId;

    @Schema(description = "清单名称")
    private String manifestName;

    @Schema(description = "版本说明")
    private String description;

    @Schema(description = "加工变量数")
    private Integer countVariable;

}
