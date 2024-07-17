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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: xiewu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "根据变量路径查询变量或公共函数 返回对象")
public class VariableDataModelRealTimeServiceUseVo {

    @Schema(description = "服务名称", example = "1")
    private Long id;

    @Schema(description = "服务名称", example = "1")
    private Long manifestId;

    @Schema(description = "服务名称", example = "1")
    private String name;

    @Schema(description = "服务编码", example = "1")
    private String code;

    @Schema(description = "服务分类", example = "1")
    private String allClass;

    @Schema(description = "角色", example = "1")
    private String manifestRole;

    @Schema(description = "状态", example = "1")
    private String state;

    @Schema(description = "已执行笔数", example = "1")
    private String currentExecuteCount;

    @Schema(description = "操作", example = "1")
    private String operate;

    //服务名称
    //服务编码
    //服务分类
    //角色
    //状态
    //已执行笔数
    //操作
}
