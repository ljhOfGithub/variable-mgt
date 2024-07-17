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

import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.enums.VarMathSymbolTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "统计分析 特殊参数配置vo")
public class ConfigSpecialMappingVo implements Serializable {

    private static final long serialVersionUID = -8474601846488576317L;

    @Schema(description = "序号", example = "1")
    private Integer serialNum;

    @Schema(description = "数据类型, STRING:string, INTEGER:int, DOUBLE:double, DATE:date, DATETIME:datetime, BOOLEAN:boolean", example = "INTEGER")
    private VarDataTypeEnum dataType;

    @Schema(description = "特殊值", example = "123")
    private String specialVal;

    @Schema(description = "操作符, INCLUDE:include, MORE_THAN:>, LESS_THAN:<, GREATER_EQUAL:>=, LESS_EQUAL:<=, EQUAL:=, EMPTY:为空", example = "INCLUDE")
    private VarMathSymbolTypeEnum operator;

}
