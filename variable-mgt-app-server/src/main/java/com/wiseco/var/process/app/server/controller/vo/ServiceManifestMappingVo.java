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
package com.wiseco.var.process.app.server.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.enums.ManifestPublishStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceManifestMappingVo implements Serializable {

    private static final long serialVersionUID = -7918525372842618791L;

    @Schema(description = "清单id")
    private Long manifestId;

    @Schema(description = "清单名称")
    private String manifestName;

    @Schema(description = "版本说明")
    private String description;

    @Schema(description = "加工变量数")
    private Integer countVariable;

    @Schema(description = "角色：1-主清单；0：异步加工清单")
    private Short role;

    @Schema(description = "true:立即生效")
    private Boolean immediateEffect;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "生效时间")
    private Date validTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "失效时间")
    private Date invalidTime;

    @Schema(description = "执行总笔数")
    private Long totalExecuteCount;

    @Schema(description = "已执行笔数", required = false)
    private Long currentExecuteCount;

    @Schema(description = "清单发布状态")
    private ManifestPublishStateEnum    manifestPublishState;
}
