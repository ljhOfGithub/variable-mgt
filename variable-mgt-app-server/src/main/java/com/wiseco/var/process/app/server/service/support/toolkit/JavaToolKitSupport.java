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
package com.wiseco.var.process.app.server.service.support.toolkit;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.decision.jsonschema.util.enums.toolkit.AttributeAccessEnum;
import com.decision.jsonschema.util.enums.toolkit.AttributeSourceTypeEnum;
import com.wiseco.boot.commons.encrypt.Md5Utils;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.ImportStatusEnum;
import com.wiseco.var.process.app.server.commons.enums.YesNoEnum;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.commons.util.GenerateIdUtil;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitAttribute;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitClass;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitJar;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitMethod;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitParameter;
import com.wiseco.var.process.app.server.service.dto.common.AttributeDTO;
import com.wiseco.var.process.app.server.service.dto.common.ClassDTO;
import com.wiseco.var.process.app.server.service.dto.common.MethodDTO;
import com.wiseco.var.process.app.server.service.dto.common.MethodDetailDTO;
import com.wiseco.var.process.app.server.service.dto.common.ParameterDTO;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.AttributeBO;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.JarParseBO;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.MethodBO;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.MethodTemplatePlaceholderBO;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.ParameterBO;
import com.wiseco.var.process.app.server.service.support.toolkit.enums.ClassBizTypeEnum;
import com.wiseco.var.process.app.server.service.support.toolkit.enums.ClassTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Java工具支持类
 */
@Slf4j
public class JavaToolKitSupport {

    public static final String JAR_FILE_EXTENSION = "jar";
    public static final String VOID_TYPE = "void";
    public static final String OBJECT_TYPE = "object";
    @Deprecated
    private static final String JAR_FILE_TEMP_UPLOAD_PATH_WINDOWS = "D:" + File.separator + "usr" + File.separator + "local" + File.separator + "src"
            + File.separator + "jarPath" + File.separator;
    @Deprecated
    private static final String JAR_FILE_TEMP_UPLOAD_PATH_LINUX = "/usr/local/src/jarPath/";
    private static final String CLASS_SUFFIX = ".class";
    private static final String WINDOWS = "Windows";

    /**
     * 根据操作系统类型获取jar文件临时上传路径
     *
     * @return 上传路径
     */
    @Deprecated
    public static String getJarFileTempUploadPath() {
        String osName = System.getProperty("os.name");
        log.info("os.name={}", osName);
        UserAgent agent = UserAgentUtil.parse(osName);
        File file;
        String jarPath;
        if (agent.getOs().getName().indexOf(WINDOWS) != MagicNumbers.MINUS_INT_1) {
            file = new File(JAR_FILE_TEMP_UPLOAD_PATH_WINDOWS);
            jarPath = JAR_FILE_TEMP_UPLOAD_PATH_WINDOWS;
        } else {
            file = new File(getFilePath(JAR_FILE_TEMP_UPLOAD_PATH_LINUX));
            jarPath = JAR_FILE_TEMP_UPLOAD_PATH_LINUX;
        }
        boolean exists = file.exists();
        if (!exists) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "创建文件夹失败!");
            }
        }
        return jarPath;
    }

    private static String getFilePath(String path) {
        return path;
    }

    /**
     * convert java type to wrl type
     *
     * @param type 类型字符串
     * @param convertObjectType 转换对象的类型
     * @return type
     */
    public static String java2WrlTypeConvert(String type, boolean convertObjectType) {
        String result = "";
        if (String.class.getTypeName().equals(type)) {
            result = DataVariableBasicTypeEnum.STRING_TYPE.getName();
        } else if (Boolean.class.getTypeName().equals(type) || boolean.class.getTypeName().equals(type)) {
            result = DataVariableBasicTypeEnum.BOOLEAN_TYPE.getName();
        } else if (Integer.class.getTypeName().equals(type) || int.class.getTypeName().equals(type)) {
            result = DataVariableBasicTypeEnum.INT_TYPE.getName();
        } else if (Double.class.getTypeName().equals(type) || double.class.getTypeName().equals(type)) {
            result = DataVariableBasicTypeEnum.DOUBLE_TYPE.getName();
        } else if (Float.class.getTypeName().equals(type) || float.class.getTypeName().equals(type)) {
            result = DataVariableBasicTypeEnum.DOUBLE_TYPE.getName();
        } else if (Date.class.getTypeName().equals(type)) {
            result = DataVariableBasicTypeEnum.DATETIME_TYPE.getName();
        } else if (void.class.getTypeName().equals(type)) {
            result = VOID_TYPE;
        } else {
            if (convertObjectType) {
                result = OBJECT_TYPE;
            } else {
                result = type;
            }
        }
        return result;
    }

    /**
     * 是否集合类型
     *
     * @param type 类型
     * @return boolean
     */
    public static boolean isArrayOrCollectionType(Class<?> type) {
        if (Objects.isNull(type)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "判断类型是否集合类型参数不能为空！");
        }
        if (ClassUtils.isPrimitiveArray(type) || ClassUtils.isAssignable(List.class, type)) {
            return true;
        }
        return false;
    }

    /**
     * 解析Jar包
     *
     * @param pathname jar包路径
     * @return JarParseBO List
     */
    @Deprecated
    public static List<JarParseBO> parseJar(String pathname) {
        if (StringUtils.isEmpty(pathname)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "jar包文件路径不能为空！");
        }
        String fileExtension = FilenameUtils.getExtension(pathname);
        if (!JAR_FILE_EXTENSION.equals(fileExtension)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "jar包文件扩展名不正确！");
        }
        URLClassLoader urlClassLoader = null;
        List<JarParseBO> jarParseBOList = new ArrayList<>(MagicNumbers.ONE_HUNDRED);
        try (JarFile jarfile = new JarFile(pathname)) {
            for (JarEntry jarEntry : Collections.list(jarfile.entries())) {
                JarParseBO jarParseBO = new JarParseBO();
                if (jarEntry.getName().endsWith(CLASS_SUFFIX)) {
                    String className = jarEntry.getName().replace('/', '.');
                    className = className.substring(0, className.lastIndexOf("."));
                    jarParseBO.setClassCanonicalName(className);
                    jarParseBOList.add(jarParseBO);
                }
            }
            String jarFileUrl = new File(pathname).getCanonicalFile().toURI().toURL().toString();
            urlClassLoader = getUrlClassLoader(jarFileUrl);
            for (JarParseBO jarParseBO : jarParseBOList) {
                Class<?> cls = Class.forName(jarParseBO.getClassCanonicalName(), false, urlClassLoader);

                // class信息处理前操作
                beforeInfoSetting(jarParseBO, cls);

                // 设置class信息
                setClassInfo(jarParseBO, cls);

                // 设置method信息
                setMethodInfo(jarParseBO, cls);

                // 设置attribute信息
                setAttributeInfo(jarParseBO, cls);

                // class信息处理后操作
                afterInfoSetting(jarParseBO, cls);
            }
        } catch (IOException ioe) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "jar文件不存在/读取异常！");
        } catch (ClassNotFoundException cnfe) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "jar包类不存在！");
        } finally {
            if (Objects.nonNull(urlClassLoader)) {
                try {
                    urlClassLoader.close();
                } catch (IOException e) {
                    log.error("关闭类加载器失败！");
                }
            }
        }
        return jarParseBOList;
    }

    private static URLClassLoader getUrlClassLoader(String jarFileUrl) throws MalformedURLException {
        return new URLClassLoader(new URL[]{new URL(jarFileUrl)});
    }

    /**
     * 解析Jar包
     *
     * @param is jar包文件输入流
     * @return JarParseBO List
     */
    public static List<JarParseBO> parseJar(InputStream is) {
        if (Objects.isNull(is)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "jar包文件路径不能为空！");
        }

        List<JarParseBO> jarParseBOList = new ArrayList<>(MagicNumbers.TWO_HUNDRED_AND_FIFTY_FIVE);

        try {
            ByteArrayOutputStream bos = JarCompressSupport.write(is);
            Map<String, byte[]> classNameBytesMapping = JarCompressSupport.unZipInMemory(bos.toByteArray());
            if (CollectionUtils.isEmpty(classNameBytesMapping)) {
                return Collections.emptyList();
            }

            ExtClassLoader extClassLoader = getExtClassLoader(classNameBytesMapping);
            for (Map.Entry<String, byte[]> entry : classNameBytesMapping.entrySet()) {
                extClassLoader.loadClass(entry.getKey());
            }

            Map<String, Class<?>> classNameClassObjectMapping = extClassLoader.getClassNameClassObjectMapping();
            if (CollectionUtils.isEmpty(classNameClassObjectMapping)) {
                return Collections.emptyList();
            }
            for (Map.Entry<String, Class<?>> classEntry : classNameClassObjectMapping.entrySet()) {
                Class<?> cls = classEntry.getValue();

                JarParseBO jarParseBO = new JarParseBO();

                // class信息处理前操作
                beforeInfoSetting(jarParseBO, cls);

                // 设置class信息
                setClassInfo(jarParseBO, cls);

                // 设置method信息
                setMethodInfo(jarParseBO, cls);

                // 设置attribute信息
                setAttributeInfo(jarParseBO, cls);

                // class信息处理后操作
                afterInfoSetting(jarParseBO, cls);

                jarParseBOList.add(jarParseBO);
            }
        } catch (IOException ioe) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "jar文件不存在/读取异常！");
        } catch (ClassNotFoundException cnfe) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "jar包类不存在！");
        }
        return jarParseBOList;
    }

    private static ExtClassLoader getExtClassLoader(Map<String, byte[]> classNameBytesMapping) {
        return new ExtClassLoader(classNameBytesMapping);
    }

    /**
     * 设置class信息
     *
     * @param bo jar转换的实体类
     * @param cls 反射类对象
     */
    public static void setClassInfo(JarParseBO bo, Class<?> cls) {
        bo.setClassCanonicalName(cls.getCanonicalName());
        bo.setClazz(cls);
        bo.setClassSimpleName(cls.getSimpleName());
        // class类型
        Integer classType = getClassType(cls);
        bo.setClassType(classType);
        // 修饰符
        Integer modifier = getClassModifier(cls);
        bo.setModifier(modifier);
    }

    /**
     * 设置method信息
     *
     * @param bo  Jar包转换的对象
     * @param cls 泛型类
     */
    public static void setMethodInfo(JarParseBO bo, Class<?> cls) {
        Method[] methods = cls.getDeclaredMethods();
        if (Objects.nonNull(methods) && methods.length > 0) {
            List<MethodBO> methodList = getMethodBos(bo, methods);
            bo.setMethods(methodList);
        } else {
            bo.setMethods(Collections.emptyList());
        }
    }

    /**
     * 获取方法列表
     *
     * @param bo      Jar转换实体类
     * @param methods 方法数组
     * @return 方法列表
     */
    private static List<MethodBO> getMethodBos(JarParseBO bo, Method[] methods) {
        List<MethodBO> methodList = new ArrayList<>(methods.length);
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            MethodBO methodBO = new MethodBO();
            methodBO.setName(method.getName());
            String methodReturnTypeName = method.getReturnType().getTypeName();
            if (javaTypeIsObject(methodReturnTypeName)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的方法[" + methodBO.getName() + "]返回值类型不能是java.lang.Object！");
            } else {
                methodBO.setReturnValueJavaType(method.getReturnType().getTypeName());
            }
            if (isArrayOrCollectionType(method.getReturnType())) {
                Type methodGenericReturnType = method.getGenericReturnType();
                if (methodGenericReturnType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) methodGenericReturnType;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                        Class<?> methodReturnActualType = (Class<?>) actualTypeArguments[0];
                        methodBO.setReturnValueWrlType(java2WrlTypeConvert(methodReturnActualType.getTypeName(), false));
                    } else {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的方法[" + methodBO.getName() + "]返回值集合的泛型数量不唯一！");
                    }
                } else {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的方法[" + methodBO.getName() + "]返回值集合未指定集合的具体泛型！");
                }
                methodBO.setReturnValueIsArray(YesNoEnum.YES.getValue());
            } else {
                methodBO.setReturnValueWrlType(java2WrlTypeConvert(method.getReturnType().getTypeName(), false));
                methodBO.setReturnValueIsArray(YesNoEnum.NO.getValue());
            }
            methodBO.setClassCanonicalName(bo.getClassCanonicalName());
            methodBO.setModifier(method.getModifiers());
            // 设置方法参数信息
            Parameter[] parameters = method.getParameters();
            if (Objects.nonNull(parameters) && parameters.length > 0) {
                List<ParameterBO> parameterBOList = new ArrayList<>(parameters.length);
                for (int i = 0; i < parameters.length; i++) {
                    ParameterBO parameterBO = new ParameterBO();
                    parameterBO.setName(parameters[i].getName());
                    String parameterTypeName = parameters[i].getType().getTypeName();
                    if (javaTypeIsObject(parameterTypeName)) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的方法[" + methodBO.getName() + "]的第" + (i + 1) + "个参数类型不能是java.lang.Object！");
                    } else {
                        parameterBO.setType(parameters[i].getType());
                        parameterBO.setJavaType(parameters[i].getType().getTypeName());
                    }
                    if (isArrayOrCollectionType(parameters[i].getType())) {
                        Type parameterGenericType = parameters[i].getParameterizedType();
                        if (parameterGenericType instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) parameterGenericType;
                            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                            if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                                Class<?> parameterActualType = (Class<?>) actualTypeArguments[0];
                                parameterBO.setWrlType(java2WrlTypeConvert(parameterActualType.getTypeName(), false));
                            } else {
                                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的方法[" + methodBO.getName() + "]中的集合参数指定的集合的泛型数量不唯一！");
                            }
                        } else {
                            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的方法[" + method.getName() + "]中的集合参数未指定集合的具体泛型！");
                        }
                        parameterBO.setIsArray(YesNoEnum.YES.getValue());
                    } else {
                        parameterBO.setWrlType(java2WrlTypeConvert(parameters[i].getType().getTypeName(), false));
                        parameterBO.setIsArray(YesNoEnum.NO.getValue());
                    }
                    parameterBO.setIdx(i);
                    parameterBOList.add(parameterBO);
                }
                methodBO.setParameters(parameterBOList);
            } else {
                methodBO.setParameters(Collections.emptyList());
            }
            methodList.add(methodBO);
        }
        return methodList;
    }

    /**
     * 设置attribute信息
     *
     * @param bo jar转换对象
     * @param cls 反射类对象
     */
    public static void setAttributeInfo(JarParseBO bo, Class<?> cls) {
        Field[] fields = cls.getDeclaredFields();
        if (Objects.nonNull(fields) && fields.length > 0) {
            List<AttributeBO> attributeList = new ArrayList<>(fields.length);
            for (Field field : fields) {
                // 私有访问权限的字段是否有getter、setter，如果没有则不显示。
                boolean containsGetterAndSetter = fieldContainsGetterAndSetter(field.getName(), bo.getMethods());
                if (Modifier.isPrivate(field.getModifiers()) && !containsGetterAndSetter) {
                    continue;
                }
                AttributeBO attributeBO = new AttributeBO();
                attributeBO.setName(field.getName());
                String attributeTypeName = field.getType().getTypeName();
                if (javaTypeIsObject(attributeTypeName)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的属性[" + attributeBO.getName() + "]类型不能是java.lang.Object！");
                } else {
                    attributeBO.setJavaType(field.getType().getTypeName());
                }
                if (isArrayOrCollectionType(field.getType())) {
                    Type fieldGenericType = field.getGenericType();
                    if (fieldGenericType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) fieldGenericType;
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                            Class<?> fieldActualType = (Class<?>) actualTypeArguments[0];
                            attributeBO.setWrlType(java2WrlTypeConvert(fieldActualType.getTypeName(), false));
                        } else {
                            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的集合属性[" + attributeBO.getName() + "]指定的集合的泛型数量不唯一！");
                        }
                    } else {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类[" + bo.getClassCanonicalName() + "]中的集合属性[" + attributeBO.getName() + "]未指定集合的具体泛型！");
                    }
                    attributeBO.setTypeIsArray(YesNoEnum.YES.getValue());
                } else {
                    attributeBO.setWrlType(java2WrlTypeConvert(field.getType().getTypeName(), false));
                    attributeBO.setTypeIsArray(YesNoEnum.NO.getValue());
                }
                attributeBO.setModifier(field.getModifiers());
                String access = getAccessByModifier(field.getName(), field.getModifiers(), bo.getMethods());
                attributeBO.setAccess(access);
                Integer sourceType = getSourceType(field.getModifiers());
                attributeBO.setSourceType(sourceType);
                attributeList.add(attributeBO);

                if (containsGetterAndSetter) {
                    // 如果属性字段有getter/setter方法，则从方法列表中移除对应的getter/setter，只保留除了属性字段之外的其他方法
                    removeAttributeGetterAndSetter(field.getName(), bo.getMethods());
                }
            }
            bo.setAttributes(attributeList);
        } else {
            bo.setAttributes(Collections.emptyList());
        }
    }

    /**
     * 额外设置class信息
     *
     * @param bo jar转换类对象
     */
    public static void setExtraClassInfo(JarParseBO bo) {
        if (Objects.isNull(bo)) {
            return;
        }
        // 方法
        List<MethodBO> methods = bo.getMethods();
        // 属性
        List<AttributeBO> attributes = bo.getAttributes();
        if (!CollectionUtils.isEmpty(methods) && !CollectionUtils.isEmpty(attributes)) {
            bo.setClassBizType(ClassBizTypeEnum.BOTH_ATTRIBUTE_METHOD.getType());
        } else if (!CollectionUtils.isEmpty(attributes) && CollectionUtils.isEmpty(methods)) {
            bo.setClassBizType(ClassBizTypeEnum.HAVE_ATTRIBUTE_NONE_METHOD.getType());
        } else if (CollectionUtils.isEmpty(attributes) && !CollectionUtils.isEmpty(methods)) {
            bo.setClassBizType(ClassBizTypeEnum.NONE_ATTRIBUTE_HAVE_METHOD.getType());
        } else {
            bo.setClassBizType(ClassBizTypeEnum.NONE_ATTRIBUTE_NONE_METHOD.getType());
        }
    }

    /**
     * 构建方法特征符
     *
     * @param returnValueJavaType 返回值的Java类型
     * @param methodName 方法名
     * @param parameterList 参数列表
     * @return 方法特征符
     */
    public static String buildMethodCharacters(String returnValueJavaType, String methodName, List<ParameterBO> parameterList) {
        StringBuilder characterBuilder = new StringBuilder();
        characterBuilder.append(returnValueJavaType).append(StringPool.SPACE).append(methodName);
        if (CollectionUtils.isEmpty(parameterList)) {
            characterBuilder.append(StringPool.LEFT_BRACKET).append(StringPool.RIGHT_BRACKET);
        } else {
            characterBuilder.append(StringPool.LEFT_BRACKET);
            for (ParameterBO parameterBO : parameterList) {
                characterBuilder.append(parameterBO.getJavaType());
                characterBuilder.append(StringPool.COMMA);
            }
            characterBuilder.deleteCharAt(characterBuilder.lastIndexOf(StringPool.COMMA));
            characterBuilder.append(StringPool.RIGHT_BRACKET);
        }
        return characterBuilder.toString();
    }

    /**
     * 构建方法模板
     *
     * @param bo 方法转换实体对象
     * @return 方法模板
     */
    public static String buildMethodTemplate(MethodBO bo) {
        if (Objects.isNull(bo)) {
            return StringPool.EMPTY;
        } else {
            StringBuilder templateBuilder = new StringBuilder();
            templateBuilder.append("计算").append(bo.getName());
            List<ParameterBO> parameters = bo.getParameters();
            if (!CollectionUtils.isEmpty(parameters)) {
                templateBuilder.append(StringPool.COMMA);
                for (int i = 0; i < parameters.size(); i++) {
                    templateBuilder
                            .append(StringPool.LEFT_CHEV)
                            .append(StringPool.DOLLAR)
                            .append(i + 1)
                            .append(StringPool.COMMA)
                            .append(
                                    getParameterZhCnType(parameters.get(i).getWrlType(),
                                            YesNoEnum.YES.getValue().equals(parameters.get(i).getIsArray()) ? true : false)).append(StringPool.RIGHT_CHEV)
                            .append(StringPool.COMMA);
                }
                templateBuilder.deleteCharAt(templateBuilder.lastIndexOf(StringPool.COMMA));
            }
            return templateBuilder.toString();
        }
    }

    /**
     * 构建Jar实体
     *
     * @param file 文件对象
     * @return Jar实体
     */
    public static JavaToolkitJar buildJavaToolkitJar(MultipartFile file) {
        try {
            JavaToolkitJar javaToolkitJar = new JavaToolkitJar();
            javaToolkitJar.setIdentifier(GenerateIdUtil.generateId());
            javaToolkitJar.setName(file.getOriginalFilename());
            javaToolkitJar.setFile(file.getBytes());
            javaToolkitJar.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
            javaToolkitJar.setCreatedUser(SessionContext.getSessionUser().getUsername());
            javaToolkitJar.setCreatedTime(DateTime.now());
            javaToolkitJar.setUpdatedUser(SessionContext.getSessionUser().getUsername());
            javaToolkitJar.setUpdatedTime(DateTime.now());
            return javaToolkitJar;
        } catch (IOException e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "jar文件读取失败！");
        }
    }

    /**
     * 构建class实体
     *
     * @param dto 输入实体类对象
     * @return class实体
     */
    public static JavaToolkitClass buildJavaToolkitClass(ClassDTO dto) {
        JavaToolkitClass javaToolkitClass = new JavaToolkitClass();
        BeanUtils.copyProperties(dto, javaToolkitClass);
        if (ImportStatusEnum.IMPORTED.getStatus().equals(javaToolkitClass.getImportStatus())) {
            javaToolkitClass.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        } else {
            javaToolkitClass.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
        }
        javaToolkitClass.setCreatedUser(SessionContext.getSessionUser().getUsername());
        javaToolkitClass.setCreatedTime(DateTime.now());
        javaToolkitClass.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        javaToolkitClass.setUpdatedTime(DateTime.now());
        return javaToolkitClass;
    }

    /**
     * 构建method实体
     *
     * @param dto 输入实体类
     * @return method实体
     */
    public static JavaToolkitMethod buildJavaToolkitMethod(MethodDTO dto) {
        JavaToolkitMethod javaToolkitMethod = new JavaToolkitMethod();
        BeanUtils.copyProperties(dto, javaToolkitMethod);
        if (ImportStatusEnum.IMPORTED.getStatus().equals(javaToolkitMethod.getImportStatus())) {
            javaToolkitMethod.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        } else {
            javaToolkitMethod.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
        }
        javaToolkitMethod.setCreatedUser(SessionContext.getSessionUser().getUsername());
        javaToolkitMethod.setCreatedTime(DateTime.now());
        javaToolkitMethod.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        javaToolkitMethod.setUpdatedTime(DateTime.now());
        return javaToolkitMethod;
    }

    /**
     * 构建parameter实体
     *
     * @param dto 输入实体类
     * @return parameter实体
     */
    public static JavaToolkitParameter buildJavaToolkitParameter(ParameterDTO dto) {
        JavaToolkitParameter javaToolkitParameter = new JavaToolkitParameter();
        BeanUtils.copyProperties(dto, javaToolkitParameter);
        javaToolkitParameter.setIdentifier(GenerateIdUtil.generateId());
        javaToolkitParameter.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        javaToolkitParameter.setCreatedUser(SessionContext.getSessionUser().getUsername());
        javaToolkitParameter.setCreatedTime(DateTime.now());
        javaToolkitParameter.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        javaToolkitParameter.setUpdatedTime(DateTime.now());
        return javaToolkitParameter;
    }

    /**
     * 构建attribute实体
     *
     * @param dto 输入实体类
     * @return attribute实体
     */
    public static JavaToolkitAttribute buildJavaToolkitAttribute(AttributeDTO dto) {
        JavaToolkitAttribute javaToolkitClassAttribute = new JavaToolkitAttribute();
        BeanUtils.copyProperties(dto, javaToolkitClassAttribute);
        if (ImportStatusEnum.IMPORTED.getStatus().equals(javaToolkitClassAttribute.getImportStatus())) {
            javaToolkitClassAttribute.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        } else {
            javaToolkitClassAttribute.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
        }
        javaToolkitClassAttribute.setCreatedUser(SessionContext.getSessionUser().getUsername());
        javaToolkitClassAttribute.setCreatedTime(DateTime.now());
        javaToolkitClassAttribute.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        javaToolkitClassAttribute.setUpdatedTime(DateTime.now());
        return javaToolkitClassAttribute;
    }

    /**
     * 校验class类型：目前只支持class，不支持abstract class、interface、enum
     *
     * @param cls 反射类对象
     */
    public static void classTypeCheck(Class<?> cls) {
        if (Objects.isNull(cls)) {
            return;
        }
        if (cls.isEnum()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不支持枚举类！");
        } else if (Modifier.isAbstract(cls.getModifiers())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不支持抽象类！");
        } else if (Modifier.isInterface(cls.getModifiers())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不支持接口！");
        }
    }

    /**
     * class类型
     *
     * @param cls 反射类对象
     * @return class类型码
     */
    public static Integer getClassType(Class<?> cls) {
        if (Objects.isNull(cls)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "参数不能为空！");
        }
        if (cls.isEnum()) {
            return ClassTypeEnum.ENUM.getType();
        } else if (Modifier.isAbstract(cls.getModifiers())) {
            return ClassTypeEnum.ABSTRACT_CLASS.getType();
        } else if (Modifier.isInterface(cls.getModifiers())) {
            return ClassTypeEnum.INTERFACE.getType();
        } else {
            return ClassTypeEnum.CLASS.getType();
        }
    }

    /**
     * 修饰符
     *
     * @param cls 反射类对象
     * @return 修饰符码
     */
    public static Integer getClassModifier(Class<?> cls) {
        if (Objects.isNull(cls)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "参数不能为空！");
        }
        return cls.getModifiers();
    }

    /**
     * 根据修饰符获取内部定义的访问权限
     *
     * @param fieldName 文件名
     * @param modifiers modifiers
     * @param methods 方法列表
     * @return 访问权限
     */
    public static String getAccessByModifier(String fieldName, int modifiers, List<MethodBO> methods) {
        // public
        if (Modifier.isPublic(modifiers)) {
            if (Modifier.isFinal(modifiers)) {
                return AttributeAccessEnum.READONLY.getAccess();
            } else {
                return AttributeAccessEnum.READ_WRITE.getAccess();
            }
        } else {
            // private
            if (fieldContainsGetterAndSetter(fieldName, methods)) {
                return AttributeAccessEnum.READ_WRITE.getAccess();
            } else {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_PERMISSION_DENIED, "访问权限获取错误！");
            }
        }
    }

    /**
     * 字段是否有getter、setter
     *
     * @param fieldName 文件名
     * @param methods 方法列表
     * @return boolean
     */
    public static boolean fieldContainsGetterAndSetter(String fieldName, List<MethodBO> methods) {
        String fieldGetMethod = "get" + fieldName;
        MethodBO getMethodBO = methods.stream().filter(item -> item.getName().equalsIgnoreCase(fieldGetMethod)).findAny().orElse(null);
        String fieldSetMethod = "set" + fieldName;
        MethodBO setMethodBO = methods.stream().filter(item -> item.getName().equalsIgnoreCase(fieldSetMethod)).findAny().orElse(null);
        return Objects.nonNull(getMethodBO) && Objects.nonNull(setMethodBO);
    }

    /**
     * 如果属性字段有getter/setter方法，则从方法列表中移除对应的getter/setter，只保留除了属性字段之外的其他方法
     *
     * @param fieldName 文件名
     * @param methods 方法列表
     */
    public static void removeAttributeGetterAndSetter(String fieldName, List<MethodBO> methods) {
        String fieldGetMethod = "get" + fieldName;
        MethodBO getMethodBO = methods.stream().filter(item -> item.getName().equalsIgnoreCase(fieldGetMethod)).findAny().orElse(null);
        if (Objects.nonNull(getMethodBO)) {
            methods.remove(getMethodBO);
        }
        String fieldSetMethod = "set" + fieldName;
        MethodBO setMethodBO = methods.stream().filter(item -> item.getName().equalsIgnoreCase(fieldSetMethod)).findAny().orElse(null);
        if (Objects.nonNull(setMethodBO)) {
            methods.remove(setMethodBO);
        }
    }

    /**
     * 获取属性来源（属性来源：1=字段，2=方法）
     *
     * @param modifiers modifiers
     * @return 属性来源码
     */
    public static Integer getSourceType(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return AttributeSourceTypeEnum.FIELD.getType();
        } else {
            return AttributeSourceTypeEnum.METHOD.getType();
        }
    }

    /**
     * 获取模板所需参数中文类型
     *
     * @param wrlType wrl类型
     * @param isArray 是否为数组
     * @return 获取模板所需参数中文类型
     */
    public static String getParameterZhCnType(String wrlType, boolean isArray) {
        String result = "null";
        if (DataVariableBasicTypeEnum.INT_TYPE.getName().equals(wrlType) || DataVariableBasicTypeEnum.DOUBLE_TYPE.getName().equals(wrlType)) {
            result = isArray ? "一个数值数组" : "一个数值";
        } else if (DataVariableBasicTypeEnum.STRING_TYPE.getName().equals(wrlType)) {
            result = isArray ? "一个字符数组" : "一个字符";
        } else if (DataVariableBasicTypeEnum.BOOLEAN_TYPE.getName().equals(wrlType)) {
            result = isArray ? "一个布尔数组" : "一个布尔";
        } else if (DataVariableBasicTypeEnum.DATETIME_TYPE.getName().equals(wrlType)) {
            result = isArray ? "一个日期数组" : "一个日期";
        } else if (OBJECT_TYPE.equals(wrlType)) {
            result = isArray ? "一个对象数组" : "一个对象";
        } else {
            result = (!isArray) ?  "一个对象" : "一个对象数组";
        }
        return result;
    }

    /**
     * 方法模板校验
     *
     * @param dto 输入实体类对象
     */
    public static void methodTemplateCheck(MethodDetailDTO dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.getMethod())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "方法模板校验参数不能为空！");
        }
        // 方法
        MethodDTO methodDTO = dto.getMethod();
        // 参数列表
        List<ParameterDTO> parameters = dto.getParameters();

        // 方法模板
        String template = methodDTO.getTemplate();
        if (StringUtils.isEmpty(template.trim())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "方法模板不能为空！");
        }

        // 校验模板后缀格式是否正确：计算赔付金额，<$1,一个数值>,<$2,一个字符>
        templateSuffixCheck(template);

        // 所有'<'索引位置
        List<Integer> leftChevIndexList = getLeftChevIndexes(template);

        // 所有'>'索引位置
        List<Integer> rightChevIndexList = getRightChevIndexes(template);

        // 再次校验
        if (leftChevIndexList.size() != rightChevIndexList.size()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "模板格式不正确！");
        }

        // 把模板后缀分组：例如<$1,一个数值>,<$2,一个字符>分为两组，第一组<$1,一个数值>,第二组<$2,一个字符>...
        List<String> templateGroups = getTemplateGroups(template, leftChevIndexList, rightChevIndexList);

        // 如果方法是无参方法，则模板不可以包含分组
        if (CollectionUtils.isEmpty(parameters) && !CollectionUtils.isEmpty(templateGroups)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "无参方法模板不能包含参数！");
        }

        // 构建方法模板占位符BO集合
        List<MethodTemplatePlaceholderBO> methodTemplatePlaceholders = buildMethodTemplatePlaceholdersBo(templateGroups);

        if (!CollectionUtils.isEmpty(parameters) && !CollectionUtils.isEmpty(methodTemplatePlaceholders)) {
            if (parameters.size() != methodTemplatePlaceholders.size()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "方法模板参数与方法参数数量不匹配！");
            }
            List<String> placeholders = methodTemplatePlaceholders.stream().map(MethodTemplatePlaceholderBO::getParameterPlaceholder).collect(Collectors.toList());
            for (int i = 0; i < methodTemplatePlaceholders.size(); i++) {
                String standardPlaceholder = StringPool.DOLLAR + (i + 1);
                if (!placeholders.contains(standardPlaceholder)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "方法模板参数占位符不匹配！");
                }
            }
        }
    }

    /**
     * 校验模板后缀格式是否正确
     *
     * @param template 模板
     */
    public static void templateSuffixCheck(String template) {
        if (StringUtils.isEmpty(template)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "方法模板不能为空！");
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
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "模板格式不正确！");
                }
            }
        }
        if (!stack.isEmpty()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "模板格式不正确！");
        }
    }

    /**
     * 所有小于索引位置
     *
     * @param template 模板
     * @return 小于的数量
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
     * @return 大于的数量
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
     * getTemplateGroups
     * @param template 模板
     * @param leftChevIndexList 小于索引的位置list
     * @param rightChevIndexList 大于索引的位置list
     * @return 模板组数
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
     * 构建方法模板占位符BO集合
     *
     * @param templateGroups 模板组
     * @return MethodTemplatePlaceholderBO List
     */
    public static List<MethodTemplatePlaceholderBO> buildMethodTemplatePlaceholdersBo(List<String> templateGroups) {
        if (CollectionUtils.isEmpty(templateGroups)) {
            return Collections.emptyList();
        }
        // 分段数
        int segmentLen = MagicNumbers.TWO;
        List<MethodTemplatePlaceholderBO> placeholders = new ArrayList<>(MagicNumbers.TEN);
        for (String templateGroup : templateGroups) {
            if (!templateGroup.contains(StringPool.COMMA)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "方法模板格式不正确！");
            }
            String[] segments = templateGroup.split(StringPool.COMMA);
            if (segments.length != segmentLen) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "方法模板格式不正确！");
            }
            MethodTemplatePlaceholderBO bo = new MethodTemplatePlaceholderBO();
            bo.setParameterPlaceholder(segments[0].replace(StringPool.LEFT_CHEV, ""));
            bo.setParameterTypeDesc(segments[1].replace(StringPool.RIGHT_CHEV, ""));
            placeholders.add(bo);
        }
        return placeholders;
    }

    /**
     * 生成类属性的identifier
     *
     * @param classCanonicalName classCanonicalName
     * @param attributeName 属性名
     * @return 类属性的identifier
     */
    public static String generateClassAttributeIdentifier(String classCanonicalName, String attributeName) {
        if (StringUtils.isEmpty(classCanonicalName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class类全路径不能为空！");
        }
        if (StringUtils.isEmpty(attributeName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "属性名不能为空！");
        }
        String identifierTemplate = classCanonicalName + StringPool.UNDERSCORE + attributeName;
        return Md5Utils.md5(identifierTemplate);
    }

    /**
     * 生成类的identifier
     *
     * @param classCanonicalName classCanonicalName
     * @return 类的identifier
     */
    public static String generateClassIdentifier(String classCanonicalName) {
        if (StringUtils.isEmpty(classCanonicalName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class类全路径不能为空！");
        }
        return Md5Utils.md5(classCanonicalName);
    }

    /**
     * 生成类方法的identifier
     *
     * @param classCanonicalName classCanonicalName
     * @param methodName 方法名
     * @param parameters 参数
     * @return 类方法的identifier
     */
    public static String generateClassMethodIdentifier(String classCanonicalName, String methodName, List<ParameterDTO> parameters) {
        if (StringUtils.isEmpty(classCanonicalName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class类全路径不能为空！");
        }
        if (StringUtils.isEmpty(methodName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "方法名不能为空！");
        }
        StringBuilder identifierBuilder = new StringBuilder();
        identifierBuilder.append(classCanonicalName).append(StringPool.UNDERSCORE).append(methodName);
        if (!CollectionUtils.isEmpty(parameters)) {
            identifierBuilder.append(StringPool.UNDERSCORE);
            for (ParameterDTO parameter : parameters) {
                identifierBuilder.append(parameter.getJavaType()).append(StringPool.UNDERSCORE);
            }
            identifierBuilder.deleteCharAt(identifierBuilder.lastIndexOf(StringPool.UNDERSCORE));
        }
        return Md5Utils.md5(identifierBuilder.toString());
    }

    /**
     * class信息处理前操作
     *
     * @param jarParseBO jar转换实体类
     * @param cls 反射类对象
     */
    public static void beforeInfoSetting(JarParseBO jarParseBO, Class<?> cls) {
        if (Objects.isNull(jarParseBO)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "jar包解析业务BO不能为空！");
        }
        if (Objects.isNull(cls)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class不能为空！");
        }
        // 校验class类型：目前只支持class，不支持abstract class、interface、enum
        classTypeCheck(cls);
    }

    /**
     * class信息处理后操作
     *
     * @param jarParseBO jar转换对象
     * @param cls 反射类对象
     */
    public static void afterInfoSetting(JarParseBO jarParseBO, Class<?> cls) {
        if (Objects.isNull(jarParseBO)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "jar包解析业务BO不能为空！");
        }
        if (Objects.isNull(cls)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class不能为空！");
        }

        // 额外设置class信息
        setExtraClassInfo(jarParseBO);
    }

    /**
     * 类型是否java.lang.Object
     *
     * @param typeName 类型名
     * @return boolean
     */
    public static boolean javaTypeIsObject(String typeName) {
        if (StringUtils.isEmpty(typeName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "判断类型是否'java.lang.Object'参数不能为空！");
        }
        return Object.class.getTypeName().equals(typeName);
    }
}
