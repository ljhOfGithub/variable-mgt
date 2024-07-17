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
package com.wiseco.var.process.app.server.service.engine;

import com.google.common.collect.Lists;
import com.wiseco.boot.commons.exception.ServiceException;
import com.wiseco.decision.engine.java.common.enums.CompileEnvEnum;
import com.wiseco.decision.engine.var.transform.component.compiler.IVarCompilerEntry;
import com.wiseco.decision.engine.var.transform.component.compiler.VarCompileResult;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import com.wisecotech.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 执行组件验证，编译
 * </p>
 *
 * @author chimeng
 * @since 2023/8/8
 */
@Service
public class EngineService {
    @Resource
    private VarProcessSpaceService varProcessSpaceService;
    @Resource
    private CompileDataBuildService compileDataBuildService;
    @Resource
    private IVarCompilerEntry varCompilerEntry;
    @Resource
    private SyntaxInfoValidService syntaxInfoValidService;

    /**
     * 验证组件，生成java代码，但不是变异成class文件
     *
     * @param type 变量测试类型
     * @param spaceId 变量空间Id
     * @param variableId 变量Id
     * @param content 内容
     * @return 变量编译验证返回Dto
     */
    public VariableCompileOutputDto validComponent(TestVariableTypeEnum type, Long spaceId, Long variableId, String content) {

        VarProcessSpace space = varProcessSpaceService.getById(spaceId);
        // 1. 获取需要验证的组件数据
        VarCompileData varCompileData = compileDataBuildService.build(space, type, variableId, content);
        // 2. 获取模板配置数据 TODO
        Map<String, JSONObject> templateConfig = null;
        // 3. 调用engine执行代码生成验证
        VarCompileResult result = varCompilerEntry.compile(varCompileData, templateConfig, false, CompileEnvEnum.TEST);
        // 4. 验证结果信息组装
        boolean success = result.isSuccess();
        // 4.1 引擎生成代码失败
        if (!success) {
            return VariableCompileOutputDto.builder().errorMessageList(defaultIfBlank(Lists.newArrayList(result.getErrorException())))
                    .warnMessageList(defaultIfBlank(result.getWarnLst())).state(false).variableId(variableId).build();
        }
        // 4.2 引擎代码生成成功，语法信息解析
        SyntaxInfoValidService.ValidMsg msg = syntaxInfoValidService.validSyntax(result.getSyntaxInfo(), varCompileData, space, type);
        return VariableCompileOutputDto.builder().errorMessageList(msg.getErrors()).warnMessageList(msg.getWarns())
                .state(CollectionUtils.isEmpty(msg.getErrors())).variableId(variableId).build();
    }

    private List<String> defaultIfBlank(List<ServiceException> exceptions) {

        return exceptions.stream()
                .map(m -> StringUtils.defaultIfBlank(m.getErrorMessage(), "未知错误，编译异常"))
                .collect(Collectors.toList());

    }
}
