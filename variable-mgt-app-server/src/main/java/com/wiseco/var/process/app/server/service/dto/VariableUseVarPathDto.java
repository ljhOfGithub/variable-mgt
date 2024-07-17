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

import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
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
public class VariableUseVarPathDto {

    @Schema(description = "变量/公共函数ID", example = "1")
    private Long id;

    @Schema(description = "变量/公共函数名称", example = "")
    private String name;

    @Schema(description = "公共函数类型", example = "")
    private FunctionTypeEnum functionType;

    @Schema(description = "中文名", example = "1")
    private String label;

    @Schema(description = "版本", example = "V1.0")
    private Integer version;

    @Schema(description = "变量路径", example = "")
    private String varPath;

    @Schema(description = "读写操作记录", example = "")
    private String actionHistory;

    @Schema(description = "参数/本地变量数据类型", example = "")
    private String parameterType;

    @Schema(description = "状态", example = "1")
    private String status;

    @Schema(description = "状态", example = "UP")
    private String statustr;

    @Schema(description = "分类", example = "1")
    private String allClass;

    @Schema(description = "使用方式", example = "1")
    private String useWay;
}
