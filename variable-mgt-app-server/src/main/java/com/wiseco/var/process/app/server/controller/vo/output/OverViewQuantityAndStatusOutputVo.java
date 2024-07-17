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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@Schema(description = "睿信概览统计数量与状态")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverViewQuantityAndStatusOutputVo implements Serializable {
    private static final long SERIAL_VERSION_UID = 8759865846955173993L;


    @Schema(description = "变量所有数量")
    private Integer variableAllNumber;

    @Schema(description = "变量所有版本数量")
    private Integer variableAllVersionNumber;

    @Schema(description = "变量启用状态数量")
    private Integer variableUpNumber;

    @Schema(description = "变量编辑状态数量")
    private Integer variableEditNumber;

    @Schema(description = "变量停用状态数量")
    private Integer variableDownNumber;


    @Schema(description = "变量清单所有数量")
    private Integer manifestAllNumber;

    @Schema(description = "变量清单启用状态数量")
    private Integer manifestUpNumber;

    @Schema(description = "变量清单编辑状态数量")
    private Integer manifestEditNumber;

    @Schema(description = "变量清单停用状态数量")
    private Integer manifestDownNumber;


    @Schema(description = "实时服务所有数量")
    private Integer serviceAllNumber;

    @Schema(description = "变量所有版本数量")
    private Integer serviceAllVersionNumber;

    @Schema(description = "实时服务启用状态数量")
    private Integer serviceUpNumber;

    @Schema(description = "实时服务编辑状态数量")
    private Integer serviceEditNumber;

    @Schema(description = "实时服务停用状态数量")
    private Integer serviceDownNumber;


    @Schema(description = "批量回溯所有数量")
    private Integer batchBacktrackingAllNumber;

    @Schema(description = "批量回溯启用状态数量")
    private Integer batchBacktrackingUpNumber;

    @Schema(description = "批量回溯编辑状态数量")
    private Integer batchBacktrackingEditNumber;

    @Schema(description = "批量回溯停用状态数量")
    private Integer batchBacktrackingDownNumber;



    @Schema(description = "预处理所有数量")
    private Integer prepAllNumber;

    @Schema(description = "预处理启用状态数量")
    private Integer prepUpNumber;

    @Schema(description = "预处理编辑状态数量")
    private Integer prepEditNumber;

    @Schema(description = "预处理停用状态数量")
    private Integer prepDownNumber;


    @Schema(description = "变量模板所有数量")
    private Integer templateAllNumber;

    @Schema(description = "变量模板启用状态数量")
    private Integer templateUpNumber;

    @Schema(description = "变量模板编辑状态数量")
    private Integer templateEditNumber;

    @Schema(description = "变量模板停用状态数量")
    private Integer templateDownNumber;



}
