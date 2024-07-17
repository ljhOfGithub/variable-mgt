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
package com.wiseco.var.process.app.server.commons.util.biz;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.enums.ProcessTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.FunctionContentDto;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.MethodTemplatePlaceholderBO;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 词条操作业务工具类
 *
 * @author Gmm
 */
public class FunctionEntryUtil {

    public static final String ENTRY_CONTENT_CANNOT_EMPTY = "词条内容不能为空.";
    public static final String ENTRY_FORMAT_ERROR = "词条格式不正确.";
    public static final Pattern PATTERN = Pattern.compile("<\\$(\\d+),\\s*(.*?)>|([^<>]*)");

    /**
     * 生成业务词条字符串
     * @param parameters 模版入参
     * @param templateName 模版名称
     * @return 业务词条字符串
     */
    public static String buildBusinessEntry(List<FunctionContentDto.LocalVar> parameters, String templateName) {
        StringBuffer entrySb = new StringBuffer();
        String validTemplateName = templateName.replaceAll(StringPool.LEFT_CHEV, StringPool.EMPTY).replaceAll(StringPool.RIGHT_CHEV, StringPool.EMPTY);
        entrySb.append("计算" + validTemplateName);
        if (!CollectionUtils.isEmpty(parameters)) {
            for (FunctionContentDto.LocalVar parameter : parameters) {
                entrySb.append(",");
                entrySb.append(String.format("<$%s,%s>", parameter.getIndex() + 1, parameter.getLabel()));
            }
        }
        return entrySb.toString();
    }

    /**
     * buildFunctionEntryContent
     *
     * @param functionEntry 函数入口
     * @param params 参数
     * @param functionId 函数Id
     * @param identifier 唯一标识符
     * @param returnType 返回类型
     * @return java.lang.String
     */
    public static String buildFunctionEntryContent(String functionEntry, List<FunctionContentDto.LocalVar> params, Long functionId, String identifier, String returnType) {

        // 规整入参
        Map<String, FunctionContentDto.LocalVar> paramMap = new HashMap<>(MagicNumbers.SIXTEEN);
        if (!CollectionUtils.isEmpty(params)) {
            // content里入参的index+1才等于词条里的$占位符下标
            params.forEach(x -> paramMap.put(String.valueOf(x.getIndex() + 1), x));
        }

        // parts
        JSONArray parts = new JSONArray();
        Matcher matcher = PATTERN.matcher(functionEntry);
        while (matcher.find()) {
            String allSeg = matcher.group(0);
            if (StringUtils.isEmpty(allSeg)) {
                continue;
            }

            JSONObject part = new JSONObject();
            String indexInSeg = matcher.group(1);
            if (indexInSeg != null) {
                // 匹配上词条参数
                String paramLabel = StringPool.LEFT_CHEV + matcher.group(MagicNumbers.TWO) + StringPool.RIGHT_CHEV;
                part.put("label", paramLabel);
                part.put("type", "param");
                part.put("opeType", "all");
                part.put("paramIndex", indexInSeg);
                FunctionContentDto.LocalVar param = paramMap.get(indexInSeg);
                part.put("paramDataType", param.getType());
                part.put("isArray", param.getIsArray());
                if (!StringUtils.isEmpty(param.getDictCode())) {
                    part.put("dictCode", param.getDictCode());
                }
            } else {
                part.put("label", allSeg);
                part.put("type", "text");
            }
            parts.add(part);
        }

        JSONObject result = new JSONObject();
        result.put("processType", ProcessTypeEnum.ENTRY);
        result.put("functionId", functionId);
        result.put("identifier", identifier);
        result.put("entryOriginString", functionEntry);
        result.put("parts", parts);
        result.put("returnType", returnType);
        return result.toJSONString();
    }

    /**
     * 词条规范
     *
     * @param functionEntry 词条
     * @param params        变量模版入参
     */
    public static void functionEntryCheck(String functionEntry, List<FunctionContentDto.LocalVar> params) {
        if (StringUtils.isEmpty(functionEntry.trim())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, ENTRY_CONTENT_CANNOT_EMPTY);
        }

        // 校验词条后缀格式是否正确：计算赔付金额，<$1,一个数值>,<$2,一个字符>
        templateSuffixCheck(functionEntry);
        // 所有'<'索引位置
        List<Integer> leftChevIndexList = getLeftChevIndexes(functionEntry);
        // 所有'>'索引位置
        List<Integer> rightChevIndexList = getRightChevIndexes(functionEntry);

        // 再次校验
        if (leftChevIndexList.size() != rightChevIndexList.size()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, ENTRY_CONTENT_CANNOT_EMPTY);
        }

        // 词条分组：例如<$1,一个数值>,<$2,一个字符>分为两组，第一组<$1,一个数值>,第二组<$2,一个字符>...
        List<String> templateGroups = getTemplateGroups(functionEntry, leftChevIndexList, rightChevIndexList);
        // 如果变量模版是无参方法，则词条不可以包含分组
        if (CollectionUtils.isEmpty(params) && !CollectionUtils.isEmpty(templateGroups)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "无参的词条不能包含参数！");
        }
        if (templateGroups.size() != params.size()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "词条参数与变量模版参数的数量不匹配！");
        }
        // 构建词条占位符BO集合
        List<MethodTemplatePlaceholderBO> methodTemplatePlaceholders = buildMethodTemplatePlaceholdersBo(templateGroups);
        if (!CollectionUtils.isEmpty(params) && !CollectionUtils.isEmpty(methodTemplatePlaceholders)) {
            if (params.size() != methodTemplatePlaceholders.size()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "词条参数与变量模版参数的数量不匹配！");
            }
            List<String> placeholders = methodTemplatePlaceholders.stream().map(MethodTemplatePlaceholderBO::getParameterPlaceholder).collect(Collectors.toList());
            for (int i = 0; i < methodTemplatePlaceholders.size(); i++) {
                String standardPlaceholder = StringPool.DOLLAR + (i + 1);
                if (!placeholders.contains(standardPlaceholder)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "词条参数占位符不匹配！");
                }
            }
        }
    }

    /**
     * 校验词条后缀格式是否正确
     *
     * @param template 模板
     */
    public static void templateSuffixCheck(String template) {
        if (StringUtils.isEmpty(template)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, ENTRY_CONTENT_CANNOT_EMPTY);
        }
        Stack<String> stack = new Stack<>();
        char[] chars = template.toCharArray();
        for (char c : chars) {
            String character = String.valueOf(c);
            if (StringPool.LEFT_CHEV.equals(character)) {
                stack.push(String.valueOf(c));
            }
            if (StringPool.RIGHT_CHEV.equals(character)) {
                String element = stack.peek();
                // 栈顶元素必须是'<'
                if (!StringUtils.isEmpty(element) && StringPool.LEFT_CHEV.equals(element)) {
                    stack.pop();
                } else {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, ENTRY_FORMAT_ERROR);
                }
            }
        }
        if (!stack.isEmpty()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, ENTRY_FORMAT_ERROR);
        }
    }

    /**
     * 所有小于的索引位置
     *
     * @param template 模板
     * @return 小于的索引位置list
     */
    public static List<Integer> getLeftChevIndexes(String template) {
        if (StringUtils.isEmpty(template)) {
            return Collections.emptyList();
        }
        List<Integer> indexes = new ArrayList<>(MagicNumbers.TEN);
        char[] chars = template.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String character = String.valueOf(chars[i]);
            if (StringPool.LEFT_CHEV.equals(character)) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    /**
     * 所有大于索引位置
     *
     * @param template 模板
     * @return 大于索引位置的list
     */
    public static List<Integer> getRightChevIndexes(String template) {
        if (StringUtils.isEmpty(template)) {
            return Collections.emptyList();
        }
        List<Integer> indexes = new ArrayList<>(MagicNumbers.TEN);
        char[] chars = template.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String character = String.valueOf(chars[i]);
            if (StringPool.RIGHT_CHEV.equals(character)) {
                indexes.add(i + 1);
            }
        }
        return indexes;
    }

    /**
     * 词条分组
     *
     * @param template 模板
     * @param leftChevIndexList 小于索引的位置
     * @param rightChevIndexList 大于索引的位置
     * @return 词条分组
     */
    public static List<String> getTemplateGroups(String template, List<Integer> leftChevIndexList, List<Integer> rightChevIndexList) {
        if (StringUtils.isEmpty(template)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(leftChevIndexList) || CollectionUtils.isEmpty(rightChevIndexList)) {
            return Collections.emptyList();
        }
        List<String> templateGroups = new ArrayList<>(MagicNumbers.TEN);
        for (int i = 0; i < leftChevIndexList.size(); i++) {
            String templateGroup = template.substring(leftChevIndexList.get(i), rightChevIndexList.get(i));
            templateGroups.add(templateGroup);
        }
        return templateGroups;
    }

    /**
     * 构建词条占位符BO集合
     *
     * @param templateGroups 词条分组
     * @return MethodTemplatePlaceholderBO的list
     */
    public static List<MethodTemplatePlaceholderBO> buildMethodTemplatePlaceholdersBo(List<String> templateGroups) {
        if (CollectionUtils.isEmpty(templateGroups)) {
            return Collections.emptyList();
        }
        // 分段数
        int segmentLen = MagicNumbers.TWO;
        List<MethodTemplatePlaceholderBO> placeholders = new ArrayList<>(MagicNumbers.TEN);
        for (String templateGroup : templateGroups) {
            if (templateGroup.indexOf(StringPool.COMMA) == MagicNumbers.MINUS_INT_1) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, ENTRY_FORMAT_ERROR);
            }
            String[] segments = templateGroup.split(StringPool.COMMA);
            if (segments.length != segmentLen) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, ENTRY_FORMAT_ERROR);
            }
            MethodTemplatePlaceholderBO bo = new MethodTemplatePlaceholderBO();
            bo.setParameterPlaceholder(segments[0].replace(StringPool.LEFT_CHEV, ""));
            bo.setParameterTypeDesc(segments[1].replace(StringPool.RIGHT_CHEV, ""));
            placeholders.add(bo);
        }
        return placeholders;
    }

}
