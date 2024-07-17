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
package com.wiseco.var.process.app.server.commons.test.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wangxianli
 * @since: 2021/11/30 19:29
 */
@Schema(description = "测试自动生成数据DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestGenerateRulesDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量全路径", example = "input.application.channelNo")
    private String name;

    @Schema(description = "变量名称", example = "属性信息")
    private String label;

    @Schema(description = "是否数组", example = "0")
    private int isArr;

    @Schema(description = "数据类型", example = "type")
    private String type;

    @Schema(description = "参数类型", example = "input.application")
    private String parameterType;

    @Schema(description = "参数是否数组", example = "0")
    private int isParameterArray;

    @Schema(description = "字段类别：0-输入，1-预期结果，2-实际结果", example = "0")
    private int fieldType;

    @Schema(description = "字典名称", example = "")
    private String enumName;

    @Schema(description = "字段值", example = "001")
    private String value;

    @Schema(description = "生成方式", example = "enum-枚举，random-随机，logic-逻辑依赖，custom-自定义")
    private String generateMode;

    @Schema(description = "生成规则", example = "")
    private String generateRule;

    @Schema(description = "生成规则描述", example = "")
    private String generateRuleDesc;

    @Schema(description = "生成规则公式", example = "")
    private String generateRuleFormula;

}

