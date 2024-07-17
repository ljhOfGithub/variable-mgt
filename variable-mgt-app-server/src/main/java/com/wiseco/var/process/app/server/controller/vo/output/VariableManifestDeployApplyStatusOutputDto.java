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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 变量清单发布申请状态出参 DTO
 *
 * @author wangxianli
 * @author Zhaoxiong Chen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单发布状态输出参数")
public class VariableManifestDeployApplyStatusOutputDto implements Serializable {

    private static final long serialVersionUID = -8700506176180432539L;

    /**
     * var_process_manifest_deploy 主键 ID
     */
    @Schema(description = "记录ID")
    private String uuid;

    @Schema(description = "发布操作整体状态", example = "1: 处理中, 2: 完成, 3: 失败")
    private Integer executeStatus;

    @Schema(description = "数据准备描述")
    private List<StepStatusVo> list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "步骤状态")
    public static class StepStatusVo implements Serializable {

        private static final long serialVersionUID = -6965672574512114510L;

        @Schema(description = "步骤", example = "1")
        private Integer step;

        @Schema(description = "步骤名称", example = "文件验证")
        private String stepName;

        @Schema(description = "处理状态：0-未开始，1-进行中，2-成功，3-失败，4-预警", example = "S")
        private Integer status;

        @Schema(description = "描述", example = "导出成功")
        private String desc;
    }
}
