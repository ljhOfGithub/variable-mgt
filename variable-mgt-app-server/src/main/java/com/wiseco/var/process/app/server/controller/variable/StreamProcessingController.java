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
package com.wiseco.var.process.app.server.controller.variable;

import com.wiseco.var.process.app.server.controller.vo.SceneListSimpleOutputVO;
import com.wiseco.var.process.app.server.enums.StreamProcessFilterConditionCmpEnum;
import com.wiseco.var.process.app.server.service.VariableContentBiz;
import com.wisecoprod.starterweb.pojo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流式变量controller
 */
@RestController
@RequestMapping("/streamVariable")
@Slf4j
@Tag(name = "流式变量")
public class StreamProcessingController {

    @Autowired
    private VariableContentBiz variableContentBiz;

    /**
     * 获取流式变量模板枚举list
     *
     * @return list
     */
    @GetMapping("/findProcessTemplates")
    @Operation(summary = "获取流式变量模板枚举")
    public ApiResult<List<SceneListSimpleOutputVO.ProcessTemplateOutputDto>> findProcessTemplates() {
        return ApiResult.success(variableContentBiz.findProcessTemplates());
    }

    /**
     * 获取流式变量计算函数枚举list
     *
     * @return list
     */
    @GetMapping("/findCalculateFunctions")
    @Operation(summary = "获取计算函数枚举")
    public ApiResult<List<SceneListSimpleOutputVO.CalculateFunctionOutputDto>> findCalculateFunctions() {
        return ApiResult.success(variableContentBiz.findCalculateFunctions());
    }

    /**
     * 获取流式变量计算函数枚举list
     * @param dataType 数据类型
     * @return list
     */
    @GetMapping("/finComparisonsByDataType")
    @Operation(summary = "获取比较符号")
    public ApiResult<List<StreamProcessFilterConditionCmpEnum>> finComparisonsByDataType(@RequestParam("dataType") @Parameter(description = "数据类型(int/double/string/date/datetime)") String dataType) {
        return ApiResult.success(variableContentBiz.finComparisonsByDataType(dataType));
    }
}
