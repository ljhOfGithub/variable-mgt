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

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.boot.commons.exception.ServiceException;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.controller.vo.PlanResultVo;
import com.wiseco.var.process.app.server.controller.vo.VariableProduceRecordVo;
import com.wiseco.var.process.app.server.controller.vo.VariableProduceResultVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConvertVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableParamInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariablePlanInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableProduceInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRuleInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRulePreviewDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRuleQueryDto;
import com.wiseco.var.process.app.server.enums.HandleEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessVariablePlanMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariablePlan;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableRule;
import com.wiseco.var.process.app.server.service.VarProcessCategoryService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.VariableBiz;
import com.wiseco.var.process.app.server.service.VariablePlanService;
import com.wiseco.var.process.app.server.service.VariableRuleService;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VariablePlanServiceImpl extends ServiceImpl<VarProcessVariablePlanMapper, VarProcessVariablePlan> implements VariablePlanService {

    @Autowired
    private VariablePlanService variablePlanService;


    @Autowired
    private VariableRuleService variableRuleService;

    @Autowired
    private VarProcessCategoryService varProcessCategoryService;

    @Autowired
    private VariableBiz variableBiz;

    @Resource
    private VarProcessFunctionService varProcessFunctionService;

    @Resource
    private VariableCompileBiz variableCompileBiz;

    private static final Pattern PATTERN = Pattern.compile("<(.*?)>");

    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd"));
    private static final ThreadLocal<DateFormat> TIME_FORMAT = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("HH:mm:ss"));
    private static final ThreadLocal<DateFormat> DATE_TIME_FORMAT = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @Override
    public APIResult addVariablePlan(List<VariablePlanInputDto> variablePlanInputDtoList) {
        if (CollectionUtils.isNotEmpty(variablePlanInputDtoList)) {
            List<VarProcessVariablePlan> variablePlans = new ArrayList<>();
            for (VariablePlanInputDto dto : variablePlanInputDtoList) {
                VarProcessVariablePlan plan = VarProcessVariablePlan.builder().id(dto.getId()).functionId(dto.getFunctionId()).planName(dto.getPlanName()).build();
                plan.setCreatedUser(SessionContext.getSessionUser().getUsername());
                plan.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                if (dto.getParamJson() != null) {
                    plan.setParamJson(dto.getParamJson().toJSONString());
                }
                variablePlans.add(plan);
            }
            variablePlanService.saveOrUpdateBatch(variablePlans);
        }
        return APIResult.success();
    }

    @Override
    public APIResult getVariablePlan(VariableRuleQueryDto dto) {
        List<VarProcessVariablePlan> list = lambdaQuery()
                .eq(VarProcessVariablePlan::getFunctionId, dto.getFunctionId())
                .eq(VarProcessVariablePlan::getDelFlag, 0).list();
        return APIResult.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public APIResult addVariableRule(VariableRuleInputDto dto) {
        String message = checkDto(dto);
        if (!StringUtils.isEmpty(message)) {
            return APIResult.fail(message);
        }

        //生成变量完成后就要保存用户调整后的规则
        VarProcessVariableRule rule = VarProcessVariableRule.builder()
                .functionId(dto.getFunctionId())
                .nameRule(dto.getNameRule())
                .identifierRule(dto.getIdentifierRule())
                .variableType(dto.getVariableType())
                .createdUser(SessionContext.getSessionUser().getUsername())
                .updatedUser(SessionContext.getSessionUser().getUsername())
                .build();
        VarProcessVariableRule originRule = variableRuleService.getOneByFunctionId(dto.getFunctionId());
        if (originRule != null) {
            rule.setId(originRule.getId());
            rule.setCreatedUser(originRule.getCreatedUser());
        }
        variableRuleService.saveOrUpdate(rule);

        List<PlanResultVo> dataResult = new ArrayList<>();
        List<VarProcessVariablePlan> list = lambdaQuery().eq(VarProcessVariablePlan::getFunctionId, dto.getFunctionId()).eq(VarProcessVariablePlan::getDelFlag, 0).list();
        for (VarProcessVariablePlan variablePlan : list) {
            //参数内容json
            String paramJson = variablePlan.getParamJson();
            VariableParamInputVo parametersVars = getParametersVars(paramJson);
            //参数标签
            List<String> paramList = parametersVars.getParamList();
            //校验参数跟
            checkParam(dto, paramList);
            //参数内容
            List<VariableProduceInputVo> variableProduceInputVoList = parametersVars.getVariableProduceInputVoList();
            // 进行数据替换
            List<VariableConvertVo> variableConvertVos = extracted(paramList, variableProduceInputVoList, dto.getNameRule(), dto.getIdentifierRule());
            //校验参数内容重复
            checkRepeat(variableConvertVos);
            // 数据实体封装
            Map<Long, String> categoryNameMap = varProcessCategoryService.getCategoryNameMap(1L);
            List<VariableProduceRecordVo> data = extracted(dto, variablePlan, variableConvertVos, rule, categoryNameMap);
            // 放入结果集
            dataResult.add(PlanResultVo.builder().label(variablePlan.getPlanName()).data(data).build());
        }
        if (CollectionUtils.isNotEmpty(dataResult)) {
            //校验方案之间是否有重复的变量名称+编码
            checkAllRepeat(dataResult);
        }

        return APIResult.success(VariableProduceResultVo.builder()
                .id(rule.getId())
                .functionId(rule.getFunctionId())
                .identifierRule(rule.getIdentifierRule())
                .nameRule(rule.getNameRule())
                .variableType(String.valueOf(rule.getVariableType()))
                .variableProduceResult(dataResult)
                .build());
    }

    private void checkAllRepeat(List<PlanResultVo> dataResult) {
        Set<String> fieldNameSet = new HashSet<>();
        Set<String> fieldCodeSet = new HashSet<>();
        dataResult.forEach(x -> {
            x.getData().forEach(vo -> {
                if (!fieldNameSet.add(vo.getName())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量名称:" + vo.getName() + "重复");
                }
                if (!fieldCodeSet.add(vo.getIdentifier())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量编码:" + vo.getIdentifier() + "重复");
                }
            });
        });
    }


    private void checkRepeat(List<VariableConvertVo> variableConvertVos) {
        Set<String> fieldNameSet = new HashSet<>();
        Set<String> fieldCodeSet = new HashSet<>();
        for (VariableConvertVo vo : variableConvertVos) {
            if (!fieldNameSet.add(vo.getName())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量名称:" + vo.getName() + "重复");
            }
            if (!fieldCodeSet.add(vo.getCode())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量编码:" + vo.getCode() + "重复");
            }
        }

    }

    private static void checkParam(VariableRuleInputDto dto, List<String> paramList) {
        StringBuilder message = new StringBuilder();
        StringBuilder message2 = new StringBuilder();
        for (String param : paramList) {
            if (!dto.getNameRule().contains(param)) {
                message.append(param);
            }
            if (!dto.getIdentifierRule().contains(param)) {
                message2.append(param);
            }
        }
        String paramName = message.toString();
        String paramCode = message2.toString();
        if (!StringUtils.isEmpty(paramName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "变量名称生成规则必须有" + paramName + "且与模板保存一致");
        }
        if (!StringUtils.isEmpty(paramCode)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "变量编码的生成规则必须有" + paramCode + "且与模板保存一致");
        }
    }


    private String checkDto(VariableRuleInputDto dto) {
        String message = "";
        String identifierRule = dto.getIdentifierRule();
        if (StringUtils.isEmpty(identifierRule)) {
            return "变量编码生成规则不能为空";
        }
        String nameRule = dto.getNameRule();
        if (StringUtils.isEmpty(nameRule)) {
            return "变量名称生成规则不能为空";
        }
        if (!validateEntity(identifierRule)) {
            return "变量编码的生成规则：数字、字母和下划线，且首位不能是数字";
        }
        if (StringUtils.isEmpty(dto.getVariableType())) {
            return "设置变量分类不能为空";
        }


        return message;
    }

    /**
     * validateEntity
     * @param entity 实体
     * @return boolean
     */
    public static boolean validateEntity(String entity) {
        // 正则表达式匹配规则：只包含数字、字母和下划线，且首位不能是数字
        String regex = "^[a-zA-Z_<>][a-zA-Z0-9_<>]*$";
        return entity.matches(regex);
    }


    private static List<VariableProduceRecordVo> extracted(VariableRuleInputDto dto, VarProcessVariablePlan variablePlan, List<VariableConvertVo> variableConvertVos, VarProcessVariableRule rule, Map<Long, String> categoryNameMap) {
        List<VariableProduceRecordVo> result = new ArrayList<>();
        for (VariableConvertVo inputVo : variableConvertVos) {
            result.add(VariableProduceRecordVo.builder()
                    .planName(variablePlan.getPlanName())
                    .name(inputVo.getName())
                    .variableType(categoryNameMap.get(dto.getVariableType()))
                    .identifier(inputVo.getCode())
                    .paramJson(JSONObject.toJSONString(inputVo.getVariableList()))
                    .dataType(dto.getDataType())
                    .build());
        }
        return result;
    }

    private static List<VariableConvertVo> extracted(List<String> paramList, List<VariableProduceInputVo> variableProduceInputVoList, String nameFormat, String codeFormat) {

        //把内容按lable分组
        Map<String, List<VariableProduceInputVo>> groups = variableProduceInputVoList.stream()
                .collect(Collectors.groupingBy(VariableProduceInputVo::getParam));

        Object[][] arrays = new Object[paramList.size()][];
        int i = 0;
        for (String key : paramList) {
            List<VariableProduceInputVo> variableProduceInputVos = groups.get(key);
            arrays[i] = variableProduceInputVos.toArray(new VariableProduceInputVo[variableProduceInputVos.size()]);
            i++;
        }
        List<List<Object>> permutations = generateMultiArrayPermutations(arrays);
        List<VariableConvertVo> list = new ArrayList<>();
        for (List<Object> permutation : permutations) {
            List<VariableConvertVo.Variable> variableList = new ArrayList<>();
            String convertName = "";
            String convertCode = "";
            for (Object object : permutation) {
                VariableProduceInputVo vo = JSON.parseObject(JSON.toJSONString(object), VariableProduceInputVo.class);
                if (StringUtils.isEmpty(convertName)) {
                    convertName = nameFormat;
                }
                if (StringUtils.isEmpty(convertCode)) {
                    convertCode = codeFormat;
                }
                convertName = convertName.replace(vo.getParam(), vo.getName());
                convertCode = convertCode.replace(vo.getParam(), vo.getCode());
                VariableConvertVo.Variable variable = VariableConvertVo.Variable.builder().name(vo.getName()).code(vo.getCode()).value(vo.getValue()).type(vo.getDataType()).label(vo.getLabel()).index(vo.getIndex()).build();
                variableList.add(variable);
            }

            //替换后的内容
            VariableConvertVo build = VariableConvertVo.builder().name(convertName).code(convertCode).build();
            build.setVariableList(variableList);
            list.add(build);

        }
        return list;
    }

    /**
     * saveVariableRule
     * @param dto 输入实体类对象
     * @return APIResult
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public APIResult saveVariableRule(VariableRuleInputDto dto) {
        VarProcessVariableRule rule = VarProcessVariableRule.builder()
                .functionId(dto.getFunctionId())
                .nameRule(dto.getNameRule())
                .identifierRule(dto.getIdentifierRule())
                .variableType(dto.getVariableType())
                .createdUser(SessionContext.getSessionUser().getUsername())
                .updatedUser(SessionContext.getSessionUser().getUsername())
                .build();
        VarProcessVariableRule originRule = variableRuleService.getOneByFunctionId(dto.getFunctionId());
        if (originRule != null) {
            rule.setId(originRule.getId());
            rule.setCreatedUser(originRule.getCreatedUser());
        }
        variableRuleService.saveOrUpdate(rule);

        List<PlanResultVo> variableProduceResult = dto.getVariableProduceResult();
        checkVariableExists(variableProduceResult);
        for (PlanResultVo planResult : variableProduceResult) {
            List<VariableProduceRecordVo> list = planResult.getData();
            list.stream().forEach(n -> {
                String paramJson = n.getParamJson();
                //保存数据的时候插入到变量表中
                if (!StringUtils.isEmpty(paramJson) && dto.getHandleEnum() == HandleEnum.SAVE) {
                    List<VariableConvertVo.Variable> variables = JSONArray.parseArray(paramJson, VariableConvertVo.Variable.class);
                    Map<String, String> userInputValueMap = variables.stream().collect(Collectors.toMap(
                            element -> element.getIndex(),
                            element -> element.getValue(), (key1, key2) -> key2));
                    try {
                        variableBiz.addDefaultVariable(1L, rule.getFunctionId(), n.getIdentifier(), n.getName(), rule.getVariableType(), userInputValueMap);
                    } catch (Exception e) {
                        log.error("添加到变量定义失败：{}", e);
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_ADD_ERROR, "变量名称：" + n.getName() + ",变量编码:" + n.getIdentifier() + "添加到变量定义失败");
                    }
                }
            });
        }

        //保存成功更新变量模板的状态
        if (dto.getHandleEnum() == HandleEnum.SAVE) {
            VarProcessFunction function = varProcessFunctionService.getById(dto.getFunctionId());
            function.setVariableCreated(1);
            varProcessFunctionService.updateById(function);
        }
        return APIResult.success();
    }

    /**
     * 校验变量名/编码在变量表是否已经存在
     * @param variableProduceResult
     */
    private void checkVariableExists(List<PlanResultVo> variableProduceResult) {
        List<String> names = variableProduceResult.stream()
                .flatMap(planResultVo -> planResultVo.getData().stream())
                .map(VariableProduceRecordVo::getName)
                .distinct()
                .collect(Collectors.toList());
        List<String> repeatNames = variableBiz.checkNameRepeat(names);
        List<String> codes = variableProduceResult.stream()
                .flatMap(planResultVo -> planResultVo.getData().stream())
                .map(VariableProduceRecordVo::getIdentifier)
                .distinct()
                .collect(Collectors.toList());
        List<String> repeatCodes = variableBiz.checkCodeRepeat(codes);
        StringBuilder errMsg = new StringBuilder();
        if (!CollectionUtils.isEmpty(repeatNames)) {
            errMsg.append("变量名称[").append(String.join(",",repeatNames)).append("],");
        }
        if (!CollectionUtils.isEmpty(repeatCodes)) {
            errMsg.append("变量编码[").append(String.join(",",repeatCodes)).append("],");
        }
        if (errMsg.length() > 0) {
            errMsg.deleteCharAt(errMsg.length() - 1);
            throw new ServiceException(errMsg.append("在变量定义中已存在，请检查").toString());
        }
    }

    @Override
    public APIResult deleteVariablePlanByIds(List<String> ids) {
        for (String id : ids) {
            VarProcessVariablePlan variablePlan = variablePlanService.getById(id);
            if (variablePlan == null) {
                return APIResult.fail("变量方案不存在");
            }
            variablePlan.setDelFlag(1);
            variablePlanService.updateById(variablePlan);
        }
        return APIResult.success();
    }

    @Override
    public APIResult previewVariableRule(VariableRulePreviewDto dto) {
        VariableProduceResultVo.VariableProduceResultVoBuilder builder = VariableProduceResultVo.builder();
        Long functionId = dto.getFunctionId();
        VarProcessVariableRule rule = variableRuleService.getOneByFunctionId(functionId);
        if (rule == null) {
            // 没有记录的话，后台生成默认规则返回前端回显
            VarProcessFunction function = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                    .select(VarProcessFunction::getContent, VarProcessFunction::getName)
                    .eq(VarProcessFunction::getId, dto.getFunctionId()));
            List<JSONObject> parameters = variableCompileBiz.getParametersVars(function.getContent());
            // 默认规则
            StringBuffer defaultIdentifierRuleSb = new StringBuffer();
            StringBuffer defaultNameRuleSb = new StringBuffer();
            for (JSONObject paramJson : parameters) {
                String paramName = paramJson.getString("name");
                String paramLabel = paramJson.getString("label");
                defaultIdentifierRuleSb.append(String.format("<%s>_", paramName));
                defaultNameRuleSb.append(String.format("%s为<%s>_", paramLabel, paramName));
            }
            // 结果
            String identifierRule = defaultIdentifierRuleSb.toString();
            String nameRule = defaultNameRuleSb.toString();
            String defaultNameRule = nameRule.substring(0, nameRule.length() - 1);
            builder.functionId(dto.getFunctionId())
                    .identifierRule(identifierRule.substring(0, identifierRule.length() - 1))
                    .nameRule(getFirstRuleName(function.getName(), defaultNameRule))
                    .variableType(null);
        } else {
            // 有记录的话用库里用户已经调整好的
            builder.id(rule.getId())
                    .functionId(rule.getFunctionId())
                    .identifierRule(rule.getIdentifierRule())
                    .nameRule(rule.getNameRule())
                    .variableType(String.valueOf(rule.getVariableType()));
        }
        return APIResult.success(builder.build());
    }

    private static String getFirstRuleName(String functionName, String defaultRuleName) {
        // 原始functionName匹配解析
        Matcher functionMatcher = PATTERN.matcher(functionName);
        Set<String> functionParseSet = new HashSet<>();
        while (functionMatcher.find()) {
            functionParseSet.add(functionMatcher.group(1));
        }

        // 默认生成的rule匹配解析
        Matcher defaultRuleMatcher = PATTERN.matcher(defaultRuleName);
        Set<String> defaultRuleParseSet = new HashSet<>();
        while (defaultRuleMatcher.find()) {
            defaultRuleParseSet.add(defaultRuleMatcher.group(1));
        }

        // 比对
        String result = defaultRuleName;
        if (functionParseSet.size() == defaultRuleParseSet.size()) {
            boolean allSame = true;
            for (String funcParam : functionParseSet) {
                if (!defaultRuleParseSet.contains(funcParam)) {
                    allSame = false;
                    break;
                }
            }
            if (allSame) {
                result = functionName;
            }
        }
        return result;
    }

    /**
     * 数据导出为csv文件
     *
     * @param records  数据
     * @param fieldMap 字段
     * @param csvName  文件名
     * @param response http响应
     */
    private void exportToCsv(List<Map<String, Object>> records, Map<String, String> fieldMap, String csvName, HttpServletResponse response) {
        List<String> lines = new ArrayList<>();
        //首行
        String[] array = fieldMap.values().toArray(new String[0]);
        List<String> fielList = Arrays.asList(fieldMap.keySet().toArray(new String[0]));
        lines.add(StringUtils.arrayToCommaDelimitedString(array));
        for (Map<String, Object> recordMap : records) {
            String[] convertedValues = new String[fieldMap.values().size()];
            int i = 0;
            for (String columnName : fielList) {
                if (recordMap.get(columnName.toLowerCase()) != null) {
                    if (!StringUtils.isEmpty(recordMap.get(columnName))) {
                        convertedValues[i] = String.valueOf(recordMap.get(columnName)) + "\t";
                    } else {
                        convertedValues[i] = "";
                    }
                    i++;
                }

            }
            lines.add(StringUtils.arrayToCommaDelimitedString(convertedValues));
        }
        CsvWriter writer = null;

        try {
            response.setContentType("application/csv;charset=GBK");
            response.setCharacterEncoding("GBK");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(csvName, "UTF-8"));
            writer = CsvUtil.getWriter(response.getWriter());
            writer.write(lines);
            response.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private VariableParamInputVo getParametersVars(String paramJson) {
        VariableParamInputVo paramInputVo = new VariableParamInputVo();
        if (StringUtils.isEmpty(paramJson)) {
            return null;
        }
        JSONObject componentJsonDto = JSONObject.parseObject(paramJson);
        if (!componentJsonDto.containsKey(MagicStrings.CONTENT)) {
            return null;
        }
        List<VariableProduceInputVo> variableProduceInputVos = new ArrayList<>();
        List<String> paramList = new ArrayList<>();
        JSONArray paramJsonArray = componentJsonDto.getJSONArray(MagicStrings.CONTENT);
        for (int i = 0; i < paramJsonArray.size(); i++) {
            JSONObject paramSubObject = paramJsonArray.getJSONObject(i);
            String label = paramSubObject.getString("label");
            String paramLabel = label.substring(label.indexOf("<"), label.indexOf(">") + 1);
            JSONArray data = paramSubObject.getJSONArray("data");
            String index = paramSubObject.getString("index");
            paramList.add(paramLabel);
            for (int j = 0; j < data.size(); j++) {
                JSONObject dataJson = data.getJSONObject(j);
                String name = dataJson.getString("name");
                String code = dataJson.getString("code");
                String params = dataJson.getString("params");
                String type = dataJson.getString("type");
                VariableProduceInputVo build = VariableProduceInputVo.builder()
                        .param(paramLabel)
                        .index(index)
                        .label(label)
                        .name(name)
                        .code(code)
                        .dataType(type)
                        .value(params).build();
                variableProduceInputVos.add(build);
            }
        }
        paramInputVo.setVariableProduceInputVoList(variableProduceInputVos);
        paramInputVo.setParamList(paramList);
        return paramInputVo;
    }

    /**
     * generateMultiArrayPermutations
     * @param objects 对象二维数组
     * @return 多数组的置换
     */
    public static List<List<Object>> generateMultiArrayPermutations(Object[][] objects) {
        List<List<Object>> permutations = new ArrayList<>();
        generateMultiArrayPermutationsHelper(objects, 0, new ArrayList<>(), permutations);
        return permutations;
    }

    private static void generateMultiArrayPermutationsHelper(Object[][] arrays, int currentIndex, List<Object> currentPermutation, List<List<Object>> permutations) {
        if (currentIndex == arrays.length) {
            permutations.add(new ArrayList<>(currentPermutation));
            return;
        }

        Object[] currentArray = arrays[currentIndex];
        for (int i = 0; i < currentArray.length; i++) {
            currentPermutation.add(currentArray[i]);
            generateMultiArrayPermutationsHelper(arrays, currentIndex + 1, currentPermutation, permutations);
            currentPermutation.remove(currentPermutation.size() - 1);
        }
    }

}
