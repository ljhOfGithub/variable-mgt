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
package com.wiseco.var.process.app.server.aspect;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiseco.boot.log.LogClient;
import com.wiseco.boot.log.operation.UserMenuOperationLog;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.commons.constant.MagicLogStrings;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.vo.input.VariableBatchUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelUpdateInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestConfigInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableServiceConfigInputVo;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestActionTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableActionTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableBatchActionTypeEnum;
import com.wiseco.var.process.app.server.repository.DynamicMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Resource
    private LogClient logClient;

    @Resource
    private DynamicMapper dynamicMapper;

    @Resource
    private HttpServletRequest request;

    @Value("${spring.application.name:variable-mgt-app}")
    private String appCode;

    private static final String VARIABLE_ID_LIST = "variableIdList";

    private static final String COPY_ID = "copyId";

    private static final String VAR_PROCESS_VARIABLE = "var_process_variable";

    private static final String VAR_PROCESS_AUTHORIZATION = "var_process_authorization";

    public static final String MENU_ID = "Menu-Id";

    /**
     * 切点
     */
    @Pointcut("@annotation(com.wiseco.var.process.app.server.annotation.LoggableMethod)")
    public void loggableMethod() {
    }

    /**
     * 切入
     *
     * @param joinPoint 切点
     * @return Object
     * @throws Throwable 异常
     */
    @Around("loggableMethod()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 获取类名、方法名和参数信息
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            // 获取类注解
            LoggableClass classAnnotation = joinPoint.getTarget().getClass().getAnnotation(LoggableClass.class);
            String classAnnotationValue = (classAnnotation != null) ? classAnnotation.param() : "";

            // 获取方法注解
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            LoggableMethod methodAnnotation = methodSignature.getMethod().getAnnotation(LoggableMethod.class);
            String methodAnnotationValue = (methodAnnotation != null) ? methodAnnotation.value() : "No method annotation";
            String[] loggableMethodParamNames = (methodAnnotation != null) ? methodAnnotation.params() : new String[0];
            String methodAnnotationType = (methodAnnotation != null && methodAnnotation.type() != null) ? methodAnnotation.type().getValue() : "No method annotation";

            // 获取参数值
            Object[] args = joinPoint.getArgs();
            Map<String, String> loggableMethodParamValuesMap = getParamValues(args, loggableMethodParamNames);

            // 获取动态SQL取值
            LoggableDynamicValue dynamicAnnotation = methodSignature.getMethod().getAnnotation(LoggableDynamicValue.class);
            String[] paramDynamicNames = (dynamicAnnotation != null) ? dynamicAnnotation.params() : new String[0];
            String[] commonName = getCommonName(paramDynamicNames, loggableMethodParamValuesMap);

            // 获取操作类型翻译
            methodAnnotationType = transType(classAnnotationValue, args[0], methodAnnotationType);

            // 记录日志
            logger.info("Class: {}, Method: {}, Param Names: {}, Param Values: {}", className, methodName, loggableMethodParamNames, loggableMethodParamValuesMap);
            String permissionAndOperationDetail = getPermissionAndOperationDetail(methodAnnotationValue, loggableMethodParamNames, loggableMethodParamValuesMap, commonName, classAnnotationValue, paramDynamicNames);
            // 通用日志输出
            final String menuId = request.getHeader(MENU_ID);
            logClient.logUserMenuOperation(UserMenuOperationLog.builder()
                    .userName(SessionContext.getSessionUser().getUsername())
                    .fullName(SessionContext.getSessionUser().getFullName())
                    .operationTime(LocalDateTime.now())
                    .ip(SessionContext.getSessionUser().getIp())
                    .operationType(methodAnnotationType)
                    .operationDetail(permissionAndOperationDetail)
                    .menuId(menuId)
                    .appCode(appCode)
                    .build());
        } catch (Exception e) {
            // 捕获异常并忽略
            log.error("log user operation fail",e);
        }

        // 执行原始方法
        Object result = joinPoint.proceed();

        // 记录方法执行结果
        logger.info("Method execution result: {}", result);

        return result;
    }

    private String transType(String classAnnotationValue, Object arg, String methodAnnotationType) {
        final Object value = getValue(arg, methodAnnotationType);
        if (value != null) {
            if (MagicLogStrings.CLASS_TYPE_VARIABLE.equals(classAnnotationValue)) {
                if (arg instanceof VariableBatchUpdateStatusInputDto) {
                    methodAnnotationType = VariableBatchActionTypeEnum.getStatus((Integer) value).getDesc();
                } else {
                    methodAnnotationType = VariableActionTypeEnum.getStatus((Integer) value).getDesc();
                }
            } else if (MagicLogStrings.CLASS_TYPE_VARIABLE_MANIFEST.equals(classAnnotationValue)) {
                methodAnnotationType = Objects.requireNonNull(VarProcessManifestActionTypeEnum.getActionTypeEnum((Integer) value)).getActionDescription();
            } else if (MagicLogStrings.CLASS_TYPE_VARIABLE_SERVICE.equals(classAnnotationValue)) {
                methodAnnotationType = getManifestAndServiceActionTypeName((Integer) value);
            } else {
                methodAnnotationType = ((FlowActionTypeEnum) value).getDesc();
            }
        }
        return methodAnnotationType;
    }

    private Object getValue(Object obj, String propertyName) {
        try {
            // 获取对象的类
            Class<?> cls = obj.getClass();
            // 获取指定属性
            Field field = cls.getDeclaredField(propertyName);
            field.setAccessible(true);
            // 获取属性值
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.info("NoSuchFieldException");
        }
        return null;
    }


    @SneakyThrows
    private Map<String, String> getParamValues(Object[] args, String[] paramNames) {
        Map<String, String> paramMap = new HashMap<>(MagicNumbers.THREE);
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Object arg : args) {
            if (!parameterMap.isEmpty()) {
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    String key = entry.getKey();
                    String[] values = entry.getValue();
                    if (values.length > 0) {
                        paramMap.put(key, values[0]);
                    }
                }
            } else {
                // 解析JSON请求体，获取指定参数的值
                String jsonBody = convertObjectToJsonString(arg);
                JsonNode rootNode = convertJsonStringToObject(jsonBody, JsonNode.class);

                for (String paramName : paramNames) {
                    if (arg.getClass().equals(VariableManifestConfigInputDto.class)) {
                        JsonNode dataInfoNode = rootNode.get(MagicLogStrings.MANIFEST_INFO);
                        String nameValue = dataInfoNode.get(MagicLogStrings.NAME).asText();
                        paramMap.put(paramName, nameValue);
                    } else if (arg.getClass().equals(VariableServiceConfigInputVo.class)) {
                        JsonNode dataInfoNode = rootNode.get(MagicLogStrings.SERVICE_INFO);
                        String nameValue = dataInfoNode.get(MagicLogStrings.SERVICE_NAME).asText();
                        paramMap.put(paramName, nameValue);
                    } else if (arg.getClass().equals(VariableDataModelUpdateInputVo.class)) {
                        JsonNode dataInfoNode = rootNode.get(MagicLogStrings.FIRST_PAGE_INFO);
                        String nameValue = dataInfoNode.get(MagicLogStrings.OBJECT_NAME).asText();
                        paramMap.put(paramName, nameValue);
                    } else if (rootNode.has(paramName)) {
                        if (VARIABLE_ID_LIST.equals(paramName)) {
                            JsonNode variableIdListNode = rootNode.get(VARIABLE_ID_LIST);
                            List<Long> variableIdList = new ArrayList<>();
                            for (JsonNode idNode : variableIdListNode) {
                                variableIdList.add(idNode.asLong());
                            }
                            paramMap.put(paramName, Arrays.toString(variableIdList.toArray(new Long[variableIdList.size()])));
                        } else {
                            paramMap.put(paramName, rootNode.get(paramName).asText());
                        }
                    }
                }
            }
        }
        return paramMap;
    }

    //value占位符匹配params

    /**
     * 生成操作详情
     *
     * @param methodAnnotationValue 方法注释的动作
     * @param paramNames            方法注释的参数
     * @param paramMap              直接从请求中取值
     * @param commonName            sql动态取值
     * @param paramDynamicNames     动态取值所需参数
     * @return String
     */
    private String convertToOperationDetail(String methodAnnotationValue, String[] paramNames, Map<String, String> paramMap, String[] commonName, String[] paramDynamicNames) {
        String operation = methodAnnotationValue;
        if (isAnyValueNotNull(commonName)) {
            if (MagicLogStrings.MODEL_ID.equals(paramNames[0])) {
                operation = operation.replaceFirst("%s", commonName[0]);
                String name = paramMap.get(paramNames[1]);
                operation = operation.replace("%s", name);
            } else if (paramDynamicNames[0].equals(MagicLogStrings.VAR_PROCESS_DATA_MODEL) && MagicLogStrings.OBJECT_NAME.equals(paramNames[0])) {
                operation = operation.replaceFirst("%s", paramMap.get(paramNames[0]));
                operation = operation.replace("%s", commonName[0]);
            } else if (MagicLogStrings.DIC_ID.equals(paramNames[0]) || MagicLogStrings.FUNCTION_TYPE.equals(paramNames[0]) || methodAnnotationValue.contains(MagicLogStrings.COPY_MANIFEST) || methodAnnotationValue.contains(MagicLogStrings.COPY_BACK) || methodAnnotationValue.contains(MagicLogStrings.EDIT_CATEGORY) || methodAnnotationValue.contains(MagicLogStrings.ADD_CATEGORY)) {
                operation = getOperation(methodAnnotationValue, paramNames, paramMap, commonName);
            } else if (methodAnnotationValue.contains(MagicLogStrings.DEFAULT)) {
                operation = operation.replaceFirst("%s", commonName[0]);
                operation = operation.replaceFirst("%s", dynamicMapper.getBeforeDefaultById(MagicLogStrings.VAR_PROCESS_CONFIG_DEFAULT, Long.valueOf(paramMap.get(MagicLogStrings.ID))));
                operation = operation.replace("%s", paramMap.get(paramNames[1]));
            } else if (COPY_ID.equals(paramNames[0]) && MagicLogStrings.NAME.equals(paramNames[1])) {
                operation = getOperation(methodAnnotationValue, paramNames, paramMap, commonName);
            } else if (methodAnnotationValue.contains(MagicLogStrings.ADD_VARIABLE)) {
                operation = operation.replaceFirst("%s", commonName[0]);
                operation = operation.replace("%s", "1");
            } else if (methodAnnotationValue.contains(MagicLogStrings.DELETE_DIC_DETAIL) || methodAnnotationValue.contains(MagicLogStrings.DELETE_CATEGORY) || methodAnnotationValue.contains(MagicLogStrings.EDIT_VARIABLE) || methodAnnotationValue.contains((MagicLogStrings.SERVICE_NEW_VERSION))) {
                operation = getOperations(methodAnnotationValue, commonName);
            } else if (paramNames.length == 1 && MagicLogStrings.VARIABLE_ID.equals(paramNames[0])) {
                operation = getOperations(methodAnnotationValue, commonName);
            } else if (COPY_ID.equals(paramNames[0])) {
                //预处理公共方法变量模板
                operation = operation.replaceFirst("%s", commonName[0]);
                operation = operation.replaceFirst("%s", commonName[1]);
                operation = operation.replace("%s", paramMap.get(paramNames[MagicNumbers.TWO]));
            } else if (paramNames.length == 1 && MagicLogStrings.SERVICE_ID.equals(paramNames[0])) {
                operation = operation.replace("%s", commonName[0]);
            } else if (paramNames.length == 1 && MagicLogStrings.FUNCTION_ID.equals(paramNames[0]) && methodAnnotationValue.contains(MagicLogStrings.TEMPLATE)) {
                //生成变量
                operation = operation.replaceFirst("%s", commonName[1]);
            } else if (paramNames.length == 1 && MagicLogStrings.FUNCTION_ID.equals(paramNames[0])) {
                //预处理删除
                operation = getOperations(methodAnnotationValue, commonName);
            } else if (paramNames.length == MagicNumbers.TWO && COPY_ID.equals(paramNames[1])) {
                //变量定义复制
                operation = operation.replaceFirst("%s", commonName[0]);
                operation = operation.replaceFirst("%s", commonName[1]);
                operation = operation.replaceFirst("%s", paramMap.get(paramNames[0]));
                operation = operation.replace("%s", "1");
            } else if (paramNames.length == MagicNumbers.TWO && MagicLogStrings.ACTION_TYPE.equals(paramNames[1])) {
                operation = operation.replaceFirst("%s", getActionTypeName(paramMap, paramNames[0], paramNames[1]));
                operation = operation.replaceFirst("%s", commonName[0]);
                if (paramNames[0].equals(MagicLogStrings.FUNCTION_ID) || paramNames[0].equals(MagicLogStrings.VARIABLE_ID)) {
                    operation = operation.replace("%s", commonName[1]);
                }
            } else if (VAR_PROCESS_AUTHORIZATION.equals(paramDynamicNames[0]) && paramMap.containsKey(LoggableMethodTypeEnum.UPDATE_STATUS.getValue())) {
                operation = operation.replaceFirst("%s",paramMap.get(paramNames[0]).equals(StringPool.ONE) ? LoggableMethodTypeEnum.ENABLE.getValue() : LoggableMethodTypeEnum.DOWN.getValue());
                operation = operation.replace("%s",commonName[0]);
            } else if (MagicLogStrings.VAR_PROCESS_SERVICE_VERSION.equals(paramDynamicNames[0]) && MagicLogStrings.ID.equals(paramDynamicNames[1])) {
                operation = operation.replaceFirst("%s", commonName[0]);
                operation = operation.replaceFirst("%s", commonName[1]);
            } else if (MagicLogStrings.VAR_PROCESS_SERVICE_VERSION.equals(paramDynamicNames[0]) && MagicLogStrings.COPIED_SERVICE_ID.equals(paramDynamicNames[1]) && MagicLogStrings.ID.equals(paramDynamicNames[MagicNumbers.TWO])) {
                operation = getCopyServiceString(commonName, operation);
            } else if (MagicLogStrings.VAR_PROCESS_SERVICE_VERSION.equals(paramDynamicNames[0]) && MagicLogStrings.ACTION_TYPE.equals(paramDynamicNames[1]) && MagicLogStrings.SERVICE_ID.equals(paramDynamicNames[MagicNumbers.TWO]) && MagicLogStrings.SERVICE_VERSION_ID.equals(paramDynamicNames[MagicNumbers.THREE])) {
                operation = getUpdateServiceString(commonName, operation);
            } else {
                //只查询名称字段名为name的对象名
                operation = operation.replace("%s", commonName[0]);
            }
        } else {
            if (paramNames.length > 0 && VARIABLE_ID_LIST.equals(paramNames[0])) {
                operation = operation.replaceFirst("%s", getActionTypeName(paramMap, MagicLogStrings.VARIABLE_ID, paramNames[1]));
                operation = operation.replace("%s", getBatch(paramDynamicNames, paramMap));
            } else if (methodAnnotationValue.contains(MagicLogStrings.SAVE_APPROVE_ARGS)) {
                operation = methodAnnotationValue;
            } else {
                for (String paramName : paramNames) {
                    String name = paramMap.get(paramName);
                    operation = operation.replace("%s", name);
                }
            }
        }
        return operation;
    }

    /**
     * 获取更新服务(具体的)的操作
     * @param commonName 动态取值
     * @param operation 操作
     * @return 更新服务(具体的)的操作
     */
    private String getUpdateServiceString(String[] commonName, String operation) {
        operation = operation.replaceFirst("%s", commonName[0]);
        operation = operation.replaceFirst("%s", commonName[1]);
        operation = operation.replaceFirst("%s", commonName[MagicNumbers.TWO]);
        return operation;
    }

    /**
     * 获取复制服务(具体的)的操作
     * @param commonName 动态取值
     * @param operation 操作
     * @return 复制服务(具体的)的操作
     */
    private String getCopyServiceString(String[] commonName, String operation) {
        operation = operation.replaceFirst("%s", commonName[0]);
        operation = operation.replaceFirst("%s", commonName[1]);
        operation = operation.replaceFirst("%s", commonName[0]);
        operation = operation.replaceFirst("%s", commonName[MagicNumbers.TWO]);
        return operation;
    }

    /**
     * 多次使用抽离
     * @param methodAnnotationValue 方法操作
     * @param paramNames            参数
     * @param paramMap              参数值
     * @param commonName            动态取值
     * @return String
     */
    private String getOperation(String methodAnnotationValue, String[] paramNames, Map<String, String> paramMap, String[] commonName) {
        String operation = methodAnnotationValue;
        operation = operation.replaceFirst("%s", commonName[0]);
        operation = operation.replace("%s", paramMap.get(paramNames[1]));
        return operation;
    }


    /**
     * 多次使用抽离
     *
     * @param methodAnnotationValue 方法操作
     * @param commonName            动态取值
     * @return String
     */
    private String getOperations(String methodAnnotationValue, String[] commonName) {
        String operation = methodAnnotationValue;
        operation = operation.replaceFirst("%s", commonName[0]);
        operation = operation.replace("%s", commonName[1]);
        return operation;
    }


    private String getActionTypeName(Map<String, String> paramMap, String idName, String actionName) {
        String actionTypeName = null;
        if (idName.equals(MagicLogStrings.FUNCTION_ID) || idName.equals(MagicLogStrings.ID)) {
            actionTypeName = dynamicMapper.getFunctionAndBacktrackingActionTypeName(FlowActionTypeEnum.valueOf(paramMap.get(actionName)));
        } else if (idName.equals(MagicLogStrings.VARIABLE_ID)) {
            actionTypeName = getVariableActionTypeName(Integer.valueOf(paramMap.get(actionName)));
        } else {
            actionTypeName = getManifestAndServiceActionTypeName(Integer.valueOf(paramMap.get(actionName)));
        }
        return actionTypeName;
    }

    private String getManifestAndServiceActionTypeName(Integer actionType) {
        String action = null;
        switch (actionType) {
            case 1:
                action = MagicLogStrings.SUBMIT;
                break;
            case MagicNumbers.TWO:
                action = MagicLogStrings.APPROVED;
                break;
            case MagicNumbers.THREE:
                action = MagicLogStrings.REFUSE;
                break;
            case MagicNumbers.FOUR:
                action = MagicLogStrings.RETURN_EDIT;
                break;
            case MagicNumbers.FIVE:
                action = MagicLogStrings.DOWN;
                break;
            case MagicNumbers.SIX:
                action = MagicLogStrings.UP;
                break;
            default:
        }
        return action;
    }

    private String getVariableActionTypeName(Integer actionType) {
        String action = null;
        switch (actionType) {
            case MagicNumbers.TWO:
                action = MagicLogStrings.SUBMIT;
                break;
            case MagicNumbers.THREE:
                action = MagicLogStrings.DOWN;
                break;
            case MagicNumbers.FOUR:
                action = MagicLogStrings.UP;
                break;
            case MagicNumbers.SIX:
                action = MagicLogStrings.APPROVED;
                break;
            case MagicNumbers.SEVEN:
                action = MagicLogStrings.REFUSE;
                break;
            case MagicNumbers.EIGHT:
                action = MagicLogStrings.RETURN_EDIT;
                break;
            case MagicNumbers.NINE:
                action = MagicLogStrings.DELETE;
                break;
            default:
        }
        return action;
    }

    private String getBatch(String[] paramDynamicNames, Map<String, String> paramMap) {
        String str = paramMap.get(paramDynamicNames[1]);
        List<Long> longList = Arrays.stream(str.replaceAll("\\D+", " ").trim().split(" "))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        //获取批量操作变量名
        String[] names = new String[longList.size()];
        //获取批量操作变量版本
        String[] versions = new String[longList.size()];
        for (Long id : longList) {
            String name = dynamicMapper.selectVariableNameById(paramDynamicNames[0], id);
            String version = dynamicMapper.selectVariableVersionById(paramDynamicNames[0], id);
            names[longList.indexOf(id)] = name;
            versions[longList.indexOf(id)] = version;
        }
        //拼接 变量名-版本
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < longList.size(); i++) {
            if (i > 0) {
                // 在每个"name-version"字符串之间添加逗号分隔符
                result.append(",");
            }
            // 拼接每个"name-version"字符串
            result.append(names[i]).append("-").append(versions[i]);
        }
        return result.toString();
    }

    private static boolean isAnyValueNotNull(String[] array) {
        for (String value : array) {
            if (value != null) {
                // 如果发现任意一个值不为null，立即返回true
                return true;
            }
        }
        // 如果数组中所有值都为null，返回false
        return false;
    }

    private String convertObjectToJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }


    private <T> T convertJsonStringToObject(String jsonString, Class<T> valueType) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, valueType);
    }


    private String getPermissionAndOperationDetail(String methodAnnotationValue, String[] paramNames, Map<String, String> paramMap, String[] commonName, String classAnnotationValue, String[] paramDynamicNames) {
        //获取资源编码和操作详情
        return convertToOperationDetail(methodAnnotationValue, paramNames, paramMap, commonName, paramDynamicNames);
    }

    private String getPermission(String operationDetail) {
        String permission = null;
        if (operationDetail.contains(MagicLogStrings.BATCH_SUBMIT)) {
            permission = MagicLogStrings.BATCH_SUBMIT;
        } else if (operationDetail.contains(MagicLogStrings.BATCH_UP)) {
            permission = MagicLogStrings.BATCH_UP;
        } else if (operationDetail.contains(MagicLogStrings.BATCH_DOWN)) {
            permission = MagicLogStrings.BATCH_DOWN;
        } else if (operationDetail.contains(MagicLogStrings.BATCH_RETURN_EDIT)) {
            permission = MagicLogStrings.BATCH_RETURN_EDIT;
        } else if (operationDetail.contains(MagicLogStrings.BATCH_APPROVED)) {
            permission = MagicLogStrings.BATCH_APPROVED;
        } else if (operationDetail.contains(MagicLogStrings.BATCH_REFUSE)) {
            permission = MagicLogStrings.BATCH_REFUSE;
        } else if (operationDetail.contains(MagicLogStrings.BATCH_DELETE)) {
            permission = MagicLogStrings.BATCH_DELETE;
        } else if (operationDetail.contains(MagicLogStrings.SUBMIT)) {
            permission = MagicLogStrings.SUBMIT;
        } else if (operationDetail.contains(MagicLogStrings.UP)) {
            permission = MagicLogStrings.UP;
        } else if (operationDetail.contains(MagicLogStrings.DOWN)) {
            permission = MagicLogStrings.DOWN;
        } else if (operationDetail.contains(MagicLogStrings.RETURN_EDIT)) {
            permission = MagicLogStrings.RETURN_EDIT;
        } else if (operationDetail.contains(MagicLogStrings.APPROVED)) {
            permission = MagicLogStrings.APPROVED;
        } else if (operationDetail.contains(MagicLogStrings.REFUSE)) {
            permission = MagicLogStrings.REFUSE;
        }
        return permission;
    }


    private String[] getCommonName(String[] paramDynamicNames, Map<String, String> paramMap) {
        //动态取值
        String[] commonName = new String[MagicNumbers.THREE];
        if (paramDynamicNames.length > 0) {
            if (paramDynamicNames.length > 1 && MagicLogStrings.MODEL_ID.equals(paramDynamicNames[1])) {
                commonName[0] = dynamicMapper.selectModelNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
            } else if (MagicLogStrings.FUNCTION_TYPE.equals(paramDynamicNames[0])) {
                commonName[0] = dynamicMapper.getFunctionTypeName(FunctionTypeEnum.valueOf(paramMap.get(paramDynamicNames[0])));
            } else if (VAR_PROCESS_VARIABLE.equals(paramDynamicNames[0]) && MagicLogStrings.ID.equals(paramDynamicNames[1])) {
                commonName = getVariableCommonName(paramDynamicNames, paramMap);
            } else if (VAR_PROCESS_VARIABLE.equals(paramDynamicNames[0]) && MagicLogStrings.VARIABLE_ID.equals(paramDynamicNames[1])) {
                commonName = getVariableCommonName(paramDynamicNames, paramMap);
            } else if (VAR_PROCESS_VARIABLE.equals(paramDynamicNames[0]) && COPY_ID.equals(paramDynamicNames[1])) {
                commonName = getVariableCommonName(paramDynamicNames, paramMap);
            } else if (paramDynamicNames[0].equals(VAR_PROCESS_VARIABLE) && paramDynamicNames[1].equals(MagicLogStrings.NAME)) {
                commonName[0] = paramMap.get(paramDynamicNames[1]);
            } else if (VAR_PROCESS_VARIABLE.equals(paramDynamicNames[0]) && MagicLogStrings.VARIABLE_PARENT_ID.equals(paramDynamicNames[1])) {
                //升级变量
                commonName[0] = dynamicMapper.selectVariableNameByParentId(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1].replace("Parent", ""))));
                commonName[1] = dynamicMapper.selectVariableMaxVersionByParentId(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1].replace("Parent", ""))));
            } else if (MagicLogStrings.VAR_PROCESS_REALTIME_SERVICE.equals(paramDynamicNames[0]) && MagicLogStrings.ID.equals(paramDynamicNames[1])) {
                // 通过ID，查找实时服务(与code相关的)的名称
                commonName[0] = dynamicMapper.selectServiceNameById(Long.valueOf(paramMap.get(paramDynamicNames[1])));
            } else if (MagicLogStrings.VAR_PROCESS_SERVICE_VERSION.equals(paramDynamicNames[0]) && MagicLogStrings.ID.equals(paramDynamicNames[1])) {
                // 通过ID，查找实时服务(与code相关的)的名称与最大的版本号
                commonName[0] = dynamicMapper.selectServiceNameById(Long.valueOf(paramMap.get(paramDynamicNames[1])));
                commonName[1] = String.valueOf((dynamicMapper.getMaxVersionByServiceId(Long.valueOf(paramMap.get(paramDynamicNames[1]))) + MagicNumbers.ONE));
            } else if (MagicLogStrings.VAR_PROCESS_SERVICE_VERSION.equals(paramDynamicNames[0]) && MagicLogStrings.COPIED_SERVICE_ID.equals(paramDynamicNames[1]) && MagicLogStrings.ID.equals(paramDynamicNames[MagicNumbers.TWO])) {
                commonName[0] = dynamicMapper.selectServiceNameById(Long.valueOf(paramMap.get(paramDynamicNames[MagicNumbers.TWO])));
                commonName[1] = dynamicMapper.getVersionByCopiedServiceId(Long.valueOf(paramMap.get(paramDynamicNames[1]))).toString();
                commonName[MagicNumbers.TWO] = String.valueOf((dynamicMapper.getMaxVersionByServiceId(Long.valueOf(paramMap.get(paramDynamicNames[1]))) + MagicNumbers.ONE));
            } else if (MagicLogStrings.VAR_PROCESS_SERVICE_VERSION.equals(paramDynamicNames[0]) && MagicLogStrings.ACTION_TYPE.equals(paramDynamicNames[1]) && MagicLogStrings.SERVICE_ID.equals(paramDynamicNames[MagicNumbers.TWO]) && MagicLogStrings.SERVICE_VERSION_ID.equals(paramDynamicNames[MagicNumbers.THREE])) {
                commonName[0] = getManifestAndServiceActionTypeName(Integer.valueOf(paramMap.get(paramDynamicNames[1])));
                commonName[1] = dynamicMapper.selectServiceNameById(Long.valueOf(paramMap.get(paramDynamicNames[MagicNumbers.TWO])));
                commonName[MagicNumbers.TWO] = String.valueOf(dynamicMapper.getVersionByCopiedServiceId(Long.valueOf(paramMap.get(paramDynamicNames[MagicNumbers.THREE]))));
            } else if (MagicLogStrings.VAR_PROCESS_BATCH_BACKTRACKING.equals(paramDynamicNames[0]) && COPY_ID.equals(paramDynamicNames[1])) {
                commonName[0] = dynamicMapper.selectNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
            } else if (MagicLogStrings.VAR_PROCESS_FUNCTION.equals(paramDynamicNames[0]) && COPY_ID.equals(paramDynamicNames[1])) {
                commonName = getFunction(paramDynamicNames, paramMap);
            } else if (MagicLogStrings.FUNCTION_ID.equals(paramDynamicNames[1])) {
                commonName = getFunction(paramDynamicNames, paramMap);
            } else if ((MagicLogStrings.VAR_PROCESS_CONFIG_TAG_GROUP.equals(paramDynamicNames[0]) && MagicLogStrings.ID.equals(paramDynamicNames[1]))) {
                commonName[0] = dynamicMapper.selectTagNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
            } else if ((MagicLogStrings.VAR_PROCESS_CONFIG_DEFAULT.equals(paramDynamicNames[0]) && MagicLogStrings.ID.equals(paramDynamicNames[1]))) {
                commonName[0] = dynamicMapper.getDataTypeNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
            } else if (paramDynamicNames.length == MagicNumbers.THREE && MagicLogStrings.ID.equals(paramDynamicNames[MagicNumbers.TWO])) {
                commonName[0] = dynamicMapper.selectDicAndCategoryById(paramDynamicNames[0], paramDynamicNames[1], Long.valueOf(paramMap.get(paramDynamicNames[MagicNumbers.TWO])));
                commonName[1] = dynamicMapper.selectNameById(paramDynamicNames[1], Long.valueOf(paramMap.get(paramDynamicNames[MagicNumbers.TWO])));
            } else if (MagicLogStrings.OBJECT_NAME.equals(paramDynamicNames[1])) {
                commonName[0] = dynamicMapper.selectVersionByObjectName(paramDynamicNames[0], paramMap.get(paramDynamicNames[1]));
            } else if (MagicLogStrings.ARCHETYPE_MANIFEST_ID.equals(paramDynamicNames[1]) || MagicLogStrings.MANIFEST_ID.equals(paramDynamicNames[1])) {
                commonName[0] = dynamicMapper.selectManifestNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
            } else if (MagicLogStrings.CATEGORY_ID.contains(paramDynamicNames[1])) {
                commonName[0] = dynamicMapper.getCategoryTypeName(CategoryTypeEnum.valueOf(dynamicMapper.getCategoryTypeNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])))));
                commonName[1] = dynamicMapper.selectNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
            } else if (VARIABLE_ID_LIST.equals(paramDynamicNames[1])) {
                commonName[0] = null;
                commonName[1] = null;
            } else if (paramDynamicNames.length == MagicNumbers.THREE && MagicLogStrings.CATEGORY_TYPE.equals(paramDynamicNames[MagicNumbers.TWO])) {
                commonName[0] = dynamicMapper.getCategoryTypeName(CategoryTypeEnum.valueOf(paramMap.get(paramDynamicNames[MagicNumbers.TWO])));
            } else if (VAR_PROCESS_AUTHORIZATION.equals(paramDynamicNames[0])) {
                commonName[0] = dynamicMapper.getAuthNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
            } else {
                //paramDynamicNames[0] 目标查询表名 paramDynamicNames[1] 目标查询id
                //只查询名称字段名为name的对象名
                commonName[0] = dynamicMapper.selectNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
            }
        }
        return commonName;
    }

    private String[] getVariableCommonName(String[] paramDynamicNames, Map<String, String> paramMap) {
        String[] commonName = new String[MagicNumbers.TWO];
        //删除编辑复制变量动态取值
        commonName[0] = dynamicMapper.selectVariableNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
        commonName[1] = dynamicMapper.selectVariableVersionById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
        return commonName;
    }


    private String[] getFunction(String[] paramDynamicNames, Map<String, String> paramMap) {
        String[] commonName = new String[MagicNumbers.TWO];
        //预处理公共方法变量模板动态获取操作对象
        commonName[0] = dynamicMapper.getFunctionTypeName(FunctionTypeEnum.valueOf(dynamicMapper.getFunctionTypeNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])))));
        commonName[1] = dynamicMapper.selectNameById(paramDynamicNames[0], Long.valueOf(paramMap.get(paramDynamicNames[1])));
        return commonName;
    }
}
