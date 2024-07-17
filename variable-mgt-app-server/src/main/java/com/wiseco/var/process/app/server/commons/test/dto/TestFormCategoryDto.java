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

import com.wiseco.var.process.app.server.service.dto.TestFormDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangxianli
 */
@Data
@Schema(description = "测试对象全量TestForm")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TestFormCategoryDto implements Serializable {

    private static final long serialVersionUID = -6682747118856456768L;

    @Schema(description = "输入", example = "null")
    private List<TestFormDto> inputList;

    @Schema(description = "输出", example = "null")
    private List<TestFormDto> outputList;

    @Schema(description = "引擎变量", example = "null")
    private List<TestFormDto> engineVarsList;

    @Schema(description = "外部数据", example = "null")
    private List<TestFormDto> externalDataList;

    @Schema(description = "公共模块数据", example = "null")
    private List<TestFormDto> commonDataList;

    @Schema(description = "blaze模块数据", example = "null")
    private List<TestFormDto> blazeDataList;

    @Schema(description = "外部变量", example = "null")
    private List<TestFormDto> externalVarsList;

    @Schema(description = "vars数据", example = "null")
    private List<TestFormDto> varsList;

    @Schema(description = "参数-基础类型", example = "null")
    private List<TestFormDto> baseParamList;

    @Schema(description = "本地变量-基础类型", example = "null")
    private List<TestFormDto> baseLocalList;

    @Schema(description = "参数-引用类型", example = "null")
    private List<TestFormDto> refParamList;

    @Schema(description = "本地变量-引用类型", example = "null")
    private List<TestFormDto> refLocalList;

    @Schema(description = "自定义函数", example = "null")
    private List<TestFormDto> functionReturnList;

    @Schema(description = "原始数据", example = "null")
    private List<TestFormDto> rawDataList;

}
