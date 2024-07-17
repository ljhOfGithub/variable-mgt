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
import lombok.Data;

import java.util.List;

/**
 *
 * @author: wangxianli
 * @since: 2022/3/28
 */
@Data
@Schema(description = "表单路径字段结构 出参Dto")
public class TestFormPathOutputDto {

    @Schema(description = "变量全路径", example = "input.application")
    private String index;

    @Schema(description = "变量名", example = "input.application")
    private String name;

    @Schema(description = "中文名", example = "申请信息")
    private String label;

    @Schema(description = "是否数组：0-否，1-是", example = "1")
    private String isArr;

    @Schema(description = "数据类型", example = "object")
    private String type;

    @Schema(description = "参数类型", example = "input.application")
    private String parameterType;

    @Schema(description = "参数是否数组", example = "0")
    private int isParameterArray;

    @Schema(description = "字典列表", example = "null")
    private List<TestFormDictDto> dictList;

    @Schema(description = "字段列表", example = "null")
    private List<TestFormPathOutputDto> children;
    /*
        @Schema(description = "字段值：数组情况下有值", example = "null")
        private Object value;*/

}
