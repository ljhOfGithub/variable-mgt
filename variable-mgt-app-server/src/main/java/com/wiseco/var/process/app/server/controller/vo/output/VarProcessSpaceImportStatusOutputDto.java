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

/**
 * 变量空间导入状态输出参数 DTO
 *
 * @author wangxianli
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量空间导入状态输出参数")
public class VarProcessSpaceImportStatusOutputDto implements Serializable {

    private static final long serialVersionUID = -8700506176180432539L;

    /**
     * var_process_manifest_deploy 主键 ID
     */
    @Schema(description = "空间ID")
    private String spaceId;

    @Schema(description = "操作状态", example = "1: 处理中, 2: 完成, 3: 失败")
    private Integer executeStatus;

    @Schema(description = "描述", example = "导出成功")
    private String desc;

}
