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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.controller.vo.input.EngineFunctionTemplateInputDto;
import com.wiseco.var.process.app.server.enums.template.FunctionProviderEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.EngineFunction;
import com.wiseco.var.process.app.server.service.EngineFunctionBiz;
import com.wiseco.var.process.app.server.service.EngineFunctionService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangxianli
 * @since 2022/6/7
 */
@Service
@Slf4j
public class EngineFunctionBizImpl implements EngineFunctionBiz {

    @Autowired
    private EngineFunctionService engineFunctionService;

    /**
     * 组装函数模板
     * @param baseObj JSON对象
     */
    @Override
    public void fillFunctionProvider(JSONObject baseObj) {
        List<EngineFunction> engineFunctionList = engineFunctionService.list(new QueryWrapper<EngineFunction>()
                .lambda()
                .eq(EngineFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .orderByAsc(EngineFunction::getSortOrder));
        if (CollectionUtils.isEmpty(engineFunctionList)) {
            return;
        }
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_NUMBER_PROVIDER, 0);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_STRING_PROVIDER, 0);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_BOOL_PROVIDER, 0);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_DATE_PROVIDER, 0);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_OBJECT_PROVIDER, 0);


        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_OBJECT_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_STRING_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_NUMBER_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_BOOL_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_DATE_PROVIDER, 1);
    }

    /**
     * 组装函数模板
     * @param baseObj JSON对象
     */
    @Override
    public void fillFunctionProvider(JSONObject baseObj, boolean templateNeed) {

        LambdaQueryWrapper<EngineFunction> queryWrapper = new QueryWrapper<EngineFunction>()
                .lambda()
                .eq(EngineFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .orderByAsc(EngineFunction::getSortOrder);
        if (templateNeed) {
            queryWrapper.eq(EngineFunction::getTemplateNeed, 1);
        }
        List<EngineFunction> engineFunctionList = engineFunctionService.list(queryWrapper);
        if (CollectionUtils.isEmpty(engineFunctionList)) {
            return;
        }
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_NUMBER_PROVIDER, 0);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_STRING_PROVIDER, 0);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_BOOL_PROVIDER, 0);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_DATE_PROVIDER, 0);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_OBJECT_PROVIDER, 0);


        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_OBJECT_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_STRING_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_NUMBER_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_BOOL_PROVIDER, 1);
        getFuncProvicer(baseObj, engineFunctionList, FunctionProviderEnum.FUNCTION_ARRAY_DATE_PROVIDER, 1);
    }

    /**
     * 获取内置函数EngineFunction的模版内容
     * @param inputDto 入参
     * @return 内置函数模版内容
     */
    @Override
    public JSONObject getEngineFunctionTemplate(EngineFunctionTemplateInputDto inputDto) {
        List<EngineFunction> engineFunctionList = engineFunctionService.list(new QueryWrapper<EngineFunction>()
                .lambda()
                .eq(EngineFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .eq(EngineFunction::getName, inputDto.getEngineFunctionName()));
        if (engineFunctionList.isEmpty()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不存在内置函数，EngineFunctionName：" + inputDto.getEngineFunctionName());
        }
        String content = engineFunctionList.get(0).getTemplateContent();
        return JSONObject.parseObject(content);
    }


    /**
     * 组装内置函数
     * @param baseObj
     * @param engineFunctionList 基础函数集合
     * @param functionProvider 函数类型
     * @param isArray
     */
    private void getFuncProvicer(JSONObject baseObj, List<EngineFunction> engineFunctionList, FunctionProviderEnum functionProvider, Integer isArray) {
        //过滤当前类型的函数数据
        List<EngineFunction> typeFunctionList = engineFunctionList.stream()
                .filter(item -> item.getDataType().equals(functionProvider.getDataType()) && item.getIsArray().equals(isArray))
                .collect(Collectors.toList());
        //按照分支数据对数据分组，每组组装child
        Map<String, List<EngineFunction>> groupFuncMap = typeFunctionList.stream()
                .collect(Collectors.groupingBy(EngineFunction::getGroupName));

        JSONArray items = new JSONArray();
        for (Map.Entry<String, List<EngineFunction>> entry : groupFuncMap.entrySet()) {
            List<EngineFunction> groupFuncList = entry.getValue();
            JSONArray childrenArray = new JSONArray();
            groupFuncList.forEach(item -> {
                JSONObject json = new JSONObject();
                json.put("type", "template");
                json.put("label", item.getLabel());
                json.put("name", item.getName());
                if ("scorecard".equalsIgnoreCase(item.getGroupName().trim())) {
                    // 评分卡的展示要单独调一下后端接口/template/getEngineFunctionTemplate
                    json.put("needCall", "1");
                }
                childrenArray.add(json);
                //组装模板内容
                if (item.getTemplateNeed().equals(1)) {
                    baseObj.put(item.getName(), JSON.parseObject(item.getTemplateContent()));
                }

            });
            JSONObject itemJson = new JSONObject();
            itemJson.put("label", groupFuncList.get(0).getGroupLabel());
            itemJson.put("children", childrenArray);
            items.add(itemJson);
        }
        JSONObject functionProvicerJson = new JSONObject();
        functionProvicerJson.put("type", "list");
        functionProvicerJson.put("items", items);
        baseObj.put(functionProvider.getProviderName(), functionProvicerJson);
    }
}
