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
package com.wiseco.var.process.app.server.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.enums.DataVariableSimpleTypeEnum;
import com.decision.jsonschema.util.enums.DomainModelTypeEnum;
import com.decision.jsonschema.util.enums.toolkit.ObjectTypeEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.EnableDisableEnum;
import com.wiseco.var.process.app.server.commons.enums.ImportStatusEnum;
import com.wiseco.var.process.app.server.commons.enums.YesNoEnum;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitAttribute;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitClass;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitJar;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitMethod;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitParameter;
import com.wiseco.var.process.app.server.repository.entity.StrComJavaToolkit;
import com.wiseco.var.process.app.server.service.dto.common.AddMethodDetailDTO;
import com.wiseco.var.process.app.server.service.dto.common.AttributeDTO;
import com.wiseco.var.process.app.server.service.dto.common.ClassDTO;
import com.wiseco.var.process.app.server.service.dto.common.JarDTO;
import com.wiseco.var.process.app.server.service.dto.common.JavaToolkitIdentifierDTO;
import com.wiseco.var.process.app.server.service.dto.common.MethodDTO;
import com.wiseco.var.process.app.server.service.dto.common.MethodDetailDTO;
import com.wiseco.var.process.app.server.service.dto.common.ParameterDTO;
import com.wiseco.var.process.app.server.service.dto.output.JarInfoDTO;
import com.wiseco.var.process.app.server.service.support.toolkit.JarCompressSupport;
import com.wiseco.var.process.app.server.service.support.toolkit.JavaToolKitSupport;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.AttributeBO;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.JarParseBO;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.MethodBO;
import com.wiseco.var.process.app.server.service.support.toolkit.bo.ParameterBO;
import com.wisecoprod.starterweb.pojo.ApiResult;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.javassist.ByteArrayClassPath;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtField;
import org.apache.ibatis.javassist.bytecode.AccessFlag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.service.support.toolkit.JavaToolKitSupport.JAR_FILE_EXTENSION;
import static com.wiseco.var.process.app.server.service.support.toolkit.JavaToolKitSupport.methodTemplateCheck;

/**
 * @author fudengkui
 * @since 2023-02-20 20:28
 */
@Slf4j
@Service
public class JavaToolKitBiz {

    @Autowired
    private CommonTemplateBiz commonTemplateBiz;

    @Autowired
    private JavaToolkitJarService javaToolkitJarService;

    @Autowired
    private JavaToolkitClassService javaToolkitClassService;

    @Autowired
    private JavaToolkitMethodService javaToolkitMethodService;

    @Autowired
    private JavaToolkitParameterService javaToolkitParameterService;

    @Autowired
    private JavaToolkitAttributeService javaToolkitAttributeService;

    @Autowired(required = false)
    private StrComJavaToolkitService strComJavaToolkitService;

    /**
     * 获取List
     *
     * @return List
     */
    public List<JarDTO> toolkitList() {
        List<JavaToolkitClass> javaToolkitClasses = javaToolkitClassService.listByConditions(null, null, null, ImportStatusEnum.IMPORTED.getStatus(), DeleteFlagEnum.USABLE.getCode());
        if (CollectionUtils.isEmpty(javaToolkitClasses)) {
            return Collections.emptyList();
        }
        Map<String, String> userMap = new HashMap<>(MagicNumbers.EIGHT);
        List<JarDTO> jarDTOList = new ArrayList<>(javaToolkitClasses.size());
        for (JavaToolkitClass javaToolkitClass : javaToolkitClasses) {
            JarDTO jarDTO = new JarDTO();
            // 类信息
            ClassDTO classDTO = new ClassDTO();
            BeanUtils.copyProperties(javaToolkitClass, classDTO);
            classDTO.setCreatedUser(userMap.getOrDefault(javaToolkitClass.getCreatedUser(), javaToolkitClass.getCreatedUser()));
            classDTO.setUpdatedUser(userMap.getOrDefault(javaToolkitClass.getUpdatedUser(), javaToolkitClass.getUpdatedUser()));
            jarDTO.setClazz(classDTO);

            // 方法信息
            List<JavaToolkitMethod> javaToolkitMethods = javaToolkitMethodService.listByConditions(javaToolkitClass.getIdentifier(), null, null, ImportStatusEnum.IMPORTED.getStatus(), DeleteFlagEnum.USABLE.getCode());
            if (!CollectionUtils.isEmpty(javaToolkitMethods)) {
                List<MethodDTO> methodDTOList = new ArrayList<>(javaToolkitMethods.size());
                for (JavaToolkitMethod javaToolkitMethod : javaToolkitMethods) {
                    MethodDTO methodDTO = new MethodDTO();
                    BeanUtils.copyProperties(javaToolkitMethod, methodDTO);
                    List<JavaToolkitParameter> javaToolkitParameters = javaToolkitParameterService.listByConditions(javaToolkitMethod.getIdentifier(), null, DeleteFlagEnum.USABLE.getCode());
                    if (!CollectionUtils.isEmpty(javaToolkitParameters)) {
                        List<ParameterDTO> parameterDTOList = new ArrayList<>(javaToolkitParameters.size());
                        for (JavaToolkitParameter javaToolkitParameter : javaToolkitParameters) {
                            ParameterDTO parameterDTO = new ParameterDTO();
                            BeanUtils.copyProperties(javaToolkitParameter, parameterDTO);
                            parameterDTOList.add(parameterDTO);
                        }
                        methodDTO.setParameters(parameterDTOList);
                    } else {
                        methodDTO.setParameters(Collections.emptyList());
                    }
                    methodDTOList.add(methodDTO);
                }
                jarDTO.setMethods(methodDTOList);
            } else {
                jarDTO.setMethods(Collections.emptyList());
            }

            // 属性信息
            List<JavaToolkitAttribute> javaToolkitAttributes = javaToolkitAttributeService.listByConditions(javaToolkitClass.getIdentifier(), null, null, ImportStatusEnum.IMPORTED.getStatus(), DeleteFlagEnum.USABLE.getCode());
            if (!CollectionUtils.isEmpty(javaToolkitAttributes)) {
                List<AttributeDTO> attributeDTOList = new ArrayList<>(javaToolkitAttributes.size());
                for (JavaToolkitAttribute javaToolkitAttribute : javaToolkitAttributes) {
                    AttributeDTO attributeDTO = new AttributeDTO();
                    BeanUtils.copyProperties(javaToolkitAttribute, attributeDTO);
                    attributeDTOList.add(attributeDTO);
                }
                jarDTO.setAttributes(attributeDTOList);
            } else {
                jarDTO.setAttributes(Collections.emptyList());
            }
            jarDTOList.add(jarDTO);
        }
        return jarDTOList;
    }

    /**
     * 解析jar包
     *
     * @param file 文件
     * @return List
     */
    public List<JarDTO> parseJar(MultipartFile file) {
        // 校验jar包名称是否重复
        List<JavaToolkitJar> list = javaToolkitJarService.listByName(file.getOriginalFilename());
        if (list.size() > 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "jar包名称重复！");
        }
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!JAR_FILE_EXTENSION.equals(fileExtension)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "jar包文件扩展名不正确！");
        }
        try {
            // 查询所有class的全路径名
            List<JavaToolkitClass> javaToolkitClasses = javaToolkitClassService.list(Wrappers.<JavaToolkitClass>lambdaQuery().select(JavaToolkitClass::getCanonicalName).ne(JavaToolkitClass::getImportStatus, ImportStatusEnum.REPEAT));
            List<String> allCanonicalNames = javaToolkitClasses.stream().map(JavaToolkitClass::getCanonicalName).collect(Collectors.toList());
            // 解析Jar包
            List<JarParseBO> jarParseBOList = JavaToolKitSupport.parseJar(file.getInputStream());
            List<JarDTO> result = new ArrayList<>(jarParseBOList.size());
            for (JarParseBO bo : jarParseBOList) {
                JarDTO resultDTO = new JarDTO();
                // 设置class信息
                ClassDTO classDTO = new ClassDTO();
                classDTO.setName(bo.getClassSimpleName());
                classDTO.setCanonicalName(bo.getClassCanonicalName());
                classDTO.setCanonicalNameExists(allCanonicalNames.contains(bo.getClassCanonicalName()));
                classDTO.setLabel(bo.getClassSimpleName());
                classDTO.setJarName(file.getOriginalFilename());
                classDTO.setClassType(bo.getClassType());
                classDTO.setClassBizType(bo.getClassBizType());
                classDTO.setModifier(bo.getModifier());
                classDTO.setStatus(EnableDisableEnum.ENABLE.getValue());
                classDTO.setImportStatus(ImportStatusEnum.NOT_IMPORTED.getStatus());
                resultDTO.setClazz(classDTO);
                // 设置方法信息
                List<MethodDTO> methods = new ArrayList<>(bo.getMethods().size());
                for (MethodBO methodBO : bo.getMethods()) {
                    MethodDTO methodDTO = new MethodDTO();
                    BeanUtils.copyProperties(methodBO, methodDTO);
                    methodDTO.setLabel(methodBO.getName());
                    methodDTO.setCharacters(JavaToolKitSupport.buildMethodCharacters(methodBO.getReturnValueJavaType(), methodBO.getName(), methodBO.getParameters()));
                    methodDTO.setTemplate(JavaToolKitSupport.buildMethodTemplate(methodBO));
                    methodDTO.setStatus(EnableDisableEnum.ENABLE.getValue());
                    methodDTO.setImportStatus(ImportStatusEnum.NOT_IMPORTED.getStatus());
                    methodDTO.setModifier(methodBO.getModifier());
                    methods.add(methodDTO);

                    List<ParameterBO> parameters = methodBO.getParameters();
                    if (!CollectionUtils.isEmpty(parameters)) {
                        List<ParameterDTO> parameterDTOList = new ArrayList<>(parameters.size());
                        for (ParameterBO parameterBO : parameters) {
                            ParameterDTO parameterDTO = new ParameterDTO();
                            BeanUtils.copyProperties(parameterBO, parameterDTO);
                            parameterDTOList.add(parameterDTO);
                        }
                        methodDTO.setParameters(parameterDTOList);
                    } else {
                        methodDTO.setParameters(Collections.emptyList());
                    }
                }
                resultDTO.setMethods(methods);
                // 设置属性信息
                List<AttributeDTO> attributes = new ArrayList<>(bo.getAttributes().size());
                for (AttributeBO attributeBO : bo.getAttributes()) {
                    AttributeDTO attributeDTO = new AttributeDTO();
                    BeanUtils.copyProperties(attributeBO, attributeDTO);
                    attributeDTO.setLabel(attributeBO.getName());
                    attributeDTO.setStatus(EnableDisableEnum.ENABLE.getValue());
                    attributeDTO.setImportStatus(ImportStatusEnum.NOT_IMPORTED.getStatus());
                    attributes.add(attributeDTO);
                }
                resultDTO.setAttributes(attributes);
                result.add(resultDTO);
            }
            return result;
        } catch (IOException e) {
            log.error("临时存储jar文件失败！", e);
            return Collections.emptyList();
        }
    }

    /**
     * 类名重复检查
     *
     * @param content 入参
     */
    public void classNameDuplicateCheck(String content) {
        List<JarDTO> dtoList = JSON.parseObject(content, new TypeReference<List<JarDTO>>() {
        });
        List<ClassDTO> classDTOList = dtoList.stream().map(JarDTO::getClazz).collect(Collectors.toList());
        // 导入状态的class进行类名称、显示名重复校验
        List<ClassDTO> importedClassDTOList = classDTOList.stream().filter(item -> ImportStatusEnum.IMPORTED.getStatus().equals(item.getImportStatus())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dtoList) || CollectionUtils.isEmpty(classDTOList) || CollectionUtils.isEmpty(importedClassDTOList)) {
            return;
        }

        // 类名称
        List<String> classNames = importedClassDTOList.stream().map(ClassDTO::getName).collect(Collectors.toList());
        List<JavaToolkitClass> classNameDuplicatedRecords = javaToolkitClassService.list(Wrappers.<JavaToolkitClass>lambdaQuery().select(JavaToolkitClass::getName).in(JavaToolkitClass::getName, classNames).ne(JavaToolkitClass::getImportStatus, ImportStatusEnum.REPEAT.getStatus()));
        if (!CollectionUtils.isEmpty(classNameDuplicatedRecords)) {
            List<String> duplicatedClassNames = classNameDuplicatedRecords.stream().map(JavaToolkitClass::getName).collect(Collectors.toList());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类名称重复：" + StringUtils.collectionToCommaDelimitedString(duplicatedClassNames));
        }

        // 显示名称
        List<String> labels = importedClassDTOList.stream().map(ClassDTO::getLabel).collect(Collectors.toList());
        List<JavaToolkitClass> labelDuplicatedRecords = javaToolkitClassService.list(Wrappers.<JavaToolkitClass>lambdaQuery().select(JavaToolkitClass::getLabel).in(JavaToolkitClass::getLabel, labels).ne(JavaToolkitClass::getImportStatus, ImportStatusEnum.REPEAT.getStatus()));
        if (!CollectionUtils.isEmpty(labelDuplicatedRecords)) {
            List<String> duplicatedClassLabels = labelDuplicatedRecords.stream().map(JavaToolkitClass::getLabel).collect(Collectors.toList());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "显示名重复：" + StringUtils.collectionToCommaDelimitedString(duplicatedClassLabels));
        }
    }

    /**
     * 保存jar
     *
     * @param content 入参
     * @param file 文件
     * @throws Exception 异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveJar(String content, MultipartFile file) throws Exception {
        // 保存jar
        JavaToolkitJar javaToolkitJar = JavaToolKitSupport.buildJavaToolkitJar(file);
        javaToolkitJarService.save(javaToolkitJar);
        List<JarDTO> dtoList = JSON.parseObject(content, new TypeReference<List<JarDTO>>() {
        });
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        ByteArrayOutputStream bos = JarCompressSupport.write(file.getInputStream());
        Map<String, byte[]> classNameBytesMapping = JarCompressSupport.unZipInMemory(bos.toByteArray());
        ClassPool pool = ClassPool.getDefault();
        // 保存类信息
        for (JarDTO dto : dtoList) {
            ClassDTO classDTO = dto.getClazz();
            classDTO.setJarIdentifier(javaToolkitJar.getIdentifier());
            // 类的identifier使用特定的生成规则来保证唯一性
            String classIdentifier = JavaToolKitSupport.generateClassIdentifier(classDTO.getCanonicalName());
            classDTO.setIdentifier(classIdentifier);
            JavaToolkitClass javaToolkitClass = JavaToolKitSupport.buildJavaToolkitClass(classDTO);
            //从byte[] 中加载
            byte[] b = classNameBytesMapping.get(javaToolkitClass.getCanonicalName());
            pool.insertClassPath(new ByteArrayClassPath(javaToolkitClass.getCanonicalName(), b));
            CtClass ctClass = pool.getCtClass(javaToolkitClass.getCanonicalName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            // 保存方法信息
            List<MethodDTO> methods = dto.getMethods();
            if (!CollectionUtils.isEmpty(methods)) {
                for (MethodDTO methodDTO : methods) {
                    methodDTO.setClassIdentifier(javaToolkitClass.getIdentifier());
                    // 方法的identifier使用特定的生成规则来保证唯一性
                    String methodIdentifier = JavaToolKitSupport.generateClassMethodIdentifier(javaToolkitClass.getCanonicalName(), methodDTO.getName(), methodDTO.getParameters());
                    methodDTO.setIdentifier(methodIdentifier);
                    JavaToolkitMethod javaToolkitMethod = JavaToolKitSupport.buildJavaToolkitMethod(methodDTO);
                    javaToolkitMethodService.save(javaToolkitMethod);
                    // 保存方法参数信息
                    List<ParameterDTO> parameters = methodDTO.getParameters();
                    for (ParameterDTO parameterDTO : parameters) {
                        parameterDTO.setMethodIdentifier(javaToolkitMethod.getIdentifier());
                        JavaToolkitParameter javaToolkitParameter = JavaToolKitSupport.buildJavaToolkitParameter(parameterDTO);
                        javaToolkitParameterService.save(javaToolkitParameter);
                    }

                    List<JavaToolkitParameter> javaToolkitParameters = javaToolkitParameterService.listByConditions(javaToolkitMethod.getIdentifier(), null, DeleteFlagEnum.USABLE.getCode());
                    // 方法模板编译
                    JSONObject methodTemplateCompile = commonTemplateBiz.buildJavaToolkitMethodTemplate(javaToolkitMethod, javaToolkitParameters);
                    String compiledMethodTemplate = methodTemplateCompile.toJSONString();
                    log.info("方法模板编译结果：{}", compiledMethodTemplate);
                    javaToolkitMethodService.update(Wrappers.<JavaToolkitMethod>lambdaUpdate().set(JavaToolkitMethod::getCompileTemplate, compiledMethodTemplate).eq(JavaToolkitMethod::getIdentifier, javaToolkitMethod.getIdentifier()));
                }
            }

            // 保存属性信息
            List<AttributeDTO> attributes = dto.getAttributes();
            if (!CollectionUtils.isEmpty(attributes)) {
                for (AttributeDTO attributeDTO : attributes) {
                    attributeDTO.setClassIdentifier(javaToolkitClass.getIdentifier());
                    // 属性的identifier使用特定的生成规则来保证唯一性
                    String attributeIdentifier = JavaToolKitSupport.generateClassAttributeIdentifier(javaToolkitClass.getCanonicalName(), attributeDTO.getName());
                    attributeDTO.setIdentifier(attributeIdentifier);
                    JavaToolkitAttribute javaToolkitAttribute = JavaToolKitSupport.buildJavaToolkitAttribute(attributeDTO);
                    javaToolkitAttributeService.save(javaToolkitAttribute);
                    CtField ctField = ctClass.getField(javaToolkitAttribute.getName());


                    ctField.setModifiers(AccessFlag.setPublic(ctField.getModifiers()));
                }
            }
            javaToolkitClass.setFile(ctClass.toBytecode());
            javaToolkitClassService.save(javaToolkitClass);

            // 更新class的JsonSchema
            updateClassJsonSchema(javaToolkitClass);
        }
    }

    /**
     * 获取jar包的文件信息
     *
     * @return jar文件信息DTO的List
     */
    public List<JarInfoDTO> listJar() {
        List<JavaToolkitJar> javaToolkitJars = javaToolkitJarService.list(Wrappers.<JavaToolkitJar>lambdaQuery().select(JavaToolkitJar::getIdentifier, JavaToolkitJar::getName));
        if (CollectionUtils.isEmpty(javaToolkitJars)) {
            return Collections.emptyList();
        } else {
            List<JarInfoDTO> result = new ArrayList<>(javaToolkitJars.size());
            for (JavaToolkitJar javaToolkitJar : javaToolkitJars) {
                JarInfoDTO dto = new JarInfoDTO();
                dto.setIdentifier(javaToolkitJar.getIdentifier());
                dto.setName(javaToolkitJar.getName());
                result.add(dto);
            }
            return result;
        }
    }

    /**
     * 获取 jar包DTO的信息
     *
     * @param jarIdentifier 入参
     * @return jar包DTO的List
     */
    public List<JarDTO> jarDetail(String jarIdentifier) {
        JavaToolkitJar javaToolkitJar = javaToolkitJarService.getUsableByIdentifier(jarIdentifier);
        if (Objects.isNull(javaToolkitJar)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "jar包不存在！");
        }
        List<JavaToolkitClass> javaToolkitClasses = javaToolkitClassService.listByConditions(jarIdentifier, null, null, ImportStatusEnum.NOT_IMPORTED.getStatus(), DeleteFlagEnum.DELETED.getCode());
        if (CollectionUtils.isEmpty(javaToolkitClasses)) {
            return Collections.emptyList();
        }
        List<JarDTO> result = new ArrayList<>();
        for (JavaToolkitClass javaToolkitClass : javaToolkitClasses) {
            JarDTO jarDTO = new JarDTO();
            ClassDTO classDTO = new ClassDTO();
            BeanUtils.copyProperties(javaToolkitClass, classDTO);
            jarDTO.setClazz(classDTO);

            List<JavaToolkitMethod> javaToolkitMethods = javaToolkitMethodService.listByConditions(javaToolkitClass.getIdentifier(), null, null, ImportStatusEnum.NOT_IMPORTED.getStatus(), DeleteFlagEnum.DELETED.getCode());
            if (!CollectionUtils.isEmpty(javaToolkitMethods)) {
                List<MethodDTO> classMethods = new ArrayList<>(javaToolkitMethods.size());
                for (JavaToolkitMethod javaToolkitMethod : javaToolkitMethods) {
                    MethodDTO methodDTO = new MethodDTO();
                    BeanUtils.copyProperties(javaToolkitMethod, methodDTO);
                    classMethods.add(methodDTO);
                }
                jarDTO.setMethods(classMethods);
            }

            List<JavaToolkitAttribute> javaToolkitAttributes = javaToolkitAttributeService.listByConditions(javaToolkitClass.getIdentifier(), null, null, ImportStatusEnum.NOT_IMPORTED.getStatus(), DeleteFlagEnum.DELETED.getCode());
            if (!CollectionUtils.isEmpty(javaToolkitAttributes)) {
                List<AttributeDTO> classAttributes = new ArrayList<>(javaToolkitAttributes.size());
                for (JavaToolkitAttribute javaToolkitAttribute : javaToolkitAttributes) {
                    AttributeDTO attributeDTO = new AttributeDTO();
                    BeanUtils.copyProperties(javaToolkitAttribute, attributeDTO);
                    classAttributes.add(attributeDTO);
                }
                jarDTO.setAttributes(classAttributes);
            }
            result.add(jarDTO);
        }
        return result;
    }

    /**
     * 添加javaToolkitClass
     *
     * @param dtoList jar包DTO的List
     */
    @Transactional(rollbackFor = Exception.class)
    public void addClass(List<JarDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        for (JarDTO jarDTO : dtoList) {
            // 类信息更新
            ClassDTO classDTO = jarDTO.getClazz();
            if (StringUtils.isEmpty(classDTO.getJarIdentifier())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "jar包编号不能为空！");
            }
            if (StringUtils.isEmpty(classDTO.getIdentifier())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class编号不能为空！");
            }
            JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOneByConditions(classDTO.getJarIdentifier(), classDTO.getIdentifier());
            if (Objects.nonNull(javaToolkitClass)) {
                BeanUtils.copyProperties(classDTO, javaToolkitClass);
                javaToolkitClass.setImportStatus(ImportStatusEnum.IMPORTED.getStatus());
                javaToolkitClass.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
                javaToolkitClass.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                javaToolkitClass.setUpdatedTime(DateTime.now());
                javaToolkitClassService.updateById(javaToolkitClass);
            }

            // 方法信息更新
            List<MethodDTO> methods = jarDTO.getMethods();
            Set<String> methodIdentifierSet = new HashSet<>();
            if (!CollectionUtils.isEmpty(methods)) {
                for (MethodDTO methodDTO : methods) {
                    JavaToolkitMethod javaToolkitMethod = javaToolkitMethodService.getOneByConditions(methodDTO.getClassIdentifier(), methodDTO.getIdentifier());
                    if (Objects.nonNull(javaToolkitMethod)) {
                        methodIdentifierSet.add(methodDTO.getIdentifier());
                        BeanUtils.copyProperties(methodDTO, javaToolkitMethod);
                        javaToolkitMethod.setImportStatus(ImportStatusEnum.IMPORTED.getStatus());
                        javaToolkitMethod.setStatus(EnableDisableEnum.ENABLE.getValue());
                        javaToolkitMethod.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
                        javaToolkitMethod.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                        javaToolkitMethod.setUpdatedTime(DateTime.now());
                        javaToolkitMethodService.updateById(javaToolkitMethod);
                    }
                }
                // parameters标记为可用
                javaToolkitParameterService.update(Wrappers.<JavaToolkitParameter>lambdaUpdate().set(JavaToolkitParameter::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).set(JavaToolkitParameter::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitParameter::getUpdatedTime, DateTime.now()).in(JavaToolkitParameter::getMethodIdentifier, methodIdentifierSet));
            }

            // 属性更新
            List<AttributeDTO> attributes = jarDTO.getAttributes();
            if (!CollectionUtils.isEmpty(attributes)) {
                for (AttributeDTO attributeDTO : attributes) {
                    JavaToolkitAttribute javaToolkitAttribute = javaToolkitAttributeService.getOneByConditions(attributeDTO.getClassIdentifier(), attributeDTO.getIdentifier());
                    if (Objects.nonNull(javaToolkitAttribute)) {
                        BeanUtils.copyProperties(attributeDTO, javaToolkitAttribute);
                        javaToolkitAttribute.setImportStatus(ImportStatusEnum.IMPORTED.getStatus());
                        javaToolkitAttribute.setStatus(EnableDisableEnum.ENABLE.getValue());
                        javaToolkitAttribute.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
                        javaToolkitAttribute.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                        javaToolkitAttribute.setUpdatedTime(DateTime.now());
                        javaToolkitAttributeService.updateById(javaToolkitAttribute);
                    }
                }
            }

            // 更新class的JsonSchema
            updateClassJsonSchema(javaToolkitClass);
        }
    }

    /**
     * 获取类DTO
     *
     * @param identifier 入参
     * @return 类DTO
     */
    public ClassDTO classDetail(String identifier) {
        JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOneByConditions(null, identifier);
        if (Objects.isNull(javaToolkitClass)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class记录不存在！");
        }
        // 类信息
        ClassDTO classDTO = new ClassDTO();
        BeanUtils.copyProperties(javaToolkitClass, classDTO);
        return classDTO;
    }

    /**
     * 更新javaToolkitClass
     *
     * @param classDTO 类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateClass(ClassDTO classDTO) {
        if (StringUtils.isEmpty(classDTO.getIdentifier())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class编号不能为空！");
        }
        JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOneByConditions(null, classDTO.getIdentifier());
        if (Objects.isNull(javaToolkitClass)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class记录不存在！");
        }

        // 更新
        javaToolkitClassService.update(Wrappers.<JavaToolkitClass>lambdaUpdate().set(JavaToolkitClass::getLabel, classDTO.getLabel()).set(JavaToolkitClass::getStatus, classDTO.getStatus()).eq(JavaToolkitClass::getId, javaToolkitClass.getId()));

        if (EnableDisableEnum.ENABLE.getValue().equals(classDTO.getStatus())) {
            // 显示名
            javaToolkitClass.setLabel(classDTO.getLabel());
            // 更新class的JsonSchema
            updateClassJsonSchema(javaToolkitClass);
        } else {
            // 更新
            javaToolkitClassService.update(Wrappers.<JavaToolkitClass>lambdaUpdate().set(JavaToolkitClass::getAttributeJsonSchema, null).set(JavaToolkitClass::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitClass::getUpdatedTime, DateTime.now()).eq(JavaToolkitClass::getId, javaToolkitClass.getId()));
        }
    }

    /**
     * 删除java工具类的校验
     *
     * @param dto java工具类编号DTO
     * @return 返回结果
     */
    public ApiResult<Void> deleteClassCheck(JavaToolkitIdentifierDTO dto) {
        JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOneByConditions(null, dto.getIdentifier());
        if (Objects.isNull(javaToolkitClass)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class记录不存在！");
        }
        List<JavaToolkitMethod> javaToolkitMethods = javaToolkitMethodService.listByConditions(javaToolkitClass.getIdentifier(), null, null, null, null);
        if (CollectionUtils.isEmpty(javaToolkitMethods)) {
            return ApiResult.success();
        }
        List<String> methodIdentifiers = javaToolkitMethods.stream().map(JavaToolkitMethod::getIdentifier).collect(Collectors.toList());
        List<StrComJavaToolkit> strComJavaToolkits = strComJavaToolkitService.list(Wrappers.<StrComJavaToolkit>lambdaQuery().in(StrComJavaToolkit::getMethodIdentifier, methodIdentifiers));
        //该类是否作为成员变量被引用
        List<JavaToolkitAttribute> attributeList = javaToolkitAttributeService.list(Wrappers.<JavaToolkitAttribute>lambdaQuery().eq(JavaToolkitAttribute::getWrlType, javaToolkitClass.getCanonicalName()).eq(JavaToolkitAttribute::getStatus, EnableDisableEnum.ENABLE.getValue()).eq(JavaToolkitAttribute::getImportStatus, ImportStatusEnum.IMPORTED.getStatus()).eq(JavaToolkitAttribute::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));

        if (!CollectionUtils.isEmpty(attributeList)) {
            List<String> collect = attributeList.stream().map(JavaToolkitAttribute::getClassIdentifier).collect(Collectors.toList());
            List<JavaToolkitClass> list = javaToolkitClassService.list(Wrappers.<JavaToolkitClass>lambdaQuery().select(JavaToolkitClass::getName).in(JavaToolkitClass::getIdentifier, collect));
            String classNames = String.join(",", list.stream().map(JavaToolkitClass::getName).collect(Collectors.toList()));
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该类正在被" + classNames + "引用, 不允许删除！");
        }

        // 类下的方法是否被策略引用，如果被引用，不允许删除。
        if (!CollectionUtils.isEmpty(strComJavaToolkits)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类下的方法被策略引用，不允许删除");
        }

        List<JavaToolkitAttribute> javaToolkitAttributes = javaToolkitAttributeService.listByConditions(javaToolkitClass.getIdentifier(), null, null, null, null);
        if (CollectionUtils.isEmpty(javaToolkitAttributes)) {
            return ApiResult.success();
        }
        List<String> attributeIdentifiers = javaToolkitAttributes.stream().map(JavaToolkitAttribute::getIdentifier).collect(Collectors.toList());
        List<StrComJavaToolkit> strComJavaAttributeToolkits = strComJavaToolkitService.list(Wrappers.<StrComJavaToolkit>lambdaQuery().in(StrComJavaToolkit::getAttributeIdentifier, attributeIdentifiers));
        // 类下的属性是否被策略引用，如果被引用，不允许删除。
        if (CollectionUtils.isEmpty(strComJavaAttributeToolkits)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "确认删除java类？");
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类下的属性被策略引用，不允许删除");
        }
    }

    /**
     * 删除java工具类
     *
     * @param dto java工具类编号DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteClass(JavaToolkitIdentifierDTO dto) {
        JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOneByConditions(null, dto.getIdentifier());
        if (Objects.isNull(javaToolkitClass)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class记录不存在！");
        }
        // class删除的时候是否触发jar包删除
        boolean deleteJar = deleteJarCheck(javaToolkitClass);
        if (deleteJar) {
            List<JavaToolkitClass> javaToolkitClasses = javaToolkitClassService.listByConditions(javaToolkitClass.getJarIdentifier(), null, null, null, null);
            if (!CollectionUtils.isEmpty(javaToolkitClasses)) {
                List<String> classIdentifiers = javaToolkitClasses.stream().map(JavaToolkitClass::getIdentifier).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(classIdentifiers)) {
                    List<JavaToolkitMethod> javaToolkitMethods = javaToolkitMethodService.list(Wrappers.<JavaToolkitMethod>lambdaQuery().select(JavaToolkitMethod::getIdentifier).in(JavaToolkitMethod::getClassIdentifier, classIdentifiers));
                    if (!CollectionUtils.isEmpty(javaToolkitMethods)) {
                        // 删除parameter
                        List<String> methodIdentifiers = javaToolkitMethods.stream().map(JavaToolkitMethod::getIdentifier).collect(Collectors.toList());
                        javaToolkitParameterService.remove(Wrappers.<JavaToolkitParameter>lambdaQuery().in(JavaToolkitParameter::getMethodIdentifier, methodIdentifiers));
                    }
                }

                // 删除method
                javaToolkitMethodService.remove(Wrappers.<JavaToolkitMethod>lambdaQuery().in(JavaToolkitMethod::getClassIdentifier, classIdentifiers));

                // 删除attribute
                javaToolkitAttributeService.remove(Wrappers.<JavaToolkitAttribute>lambdaQuery().in(JavaToolkitAttribute::getClassIdentifier, classIdentifiers));
            }

            // 删除class
            javaToolkitClassService.remove(Wrappers.<JavaToolkitClass>lambdaQuery().eq(JavaToolkitClass::getJarIdentifier, javaToolkitClass.getJarIdentifier()));

            // 删除jar
            javaToolkitJarService.remove(Wrappers.<JavaToolkitJar>lambdaQuery().eq(JavaToolkitJar::getIdentifier, javaToolkitClass.getJarIdentifier()));
        } else {
            // class标记为删除
            javaToolkitClassService.update(Wrappers.<JavaToolkitClass>lambdaUpdate().set(JavaToolkitClass::getAttributeJsonSchema, null).set(JavaToolkitClass::getImportStatus, ImportStatusEnum.NOT_IMPORTED.getStatus()).set(JavaToolkitClass::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()).set(JavaToolkitClass::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitClass::getUpdatedTime, DateTime.now()).eq(JavaToolkitClass::getId, javaToolkitClass.getId()));

            // method标记为删除
            List<JavaToolkitMethod> javaToolkitClassMethods = javaToolkitMethodService.listByConditions(javaToolkitClass.getIdentifier(), null, null, null, null);
            if (!CollectionUtils.isEmpty(javaToolkitClassMethods)) {
                List<String> methodIdentifiers = javaToolkitClassMethods.stream().map(JavaToolkitMethod::getIdentifier).collect(Collectors.toList());
                javaToolkitMethodService.update(Wrappers.<JavaToolkitMethod>lambdaUpdate().set(JavaToolkitMethod::getImportStatus, ImportStatusEnum.NOT_IMPORTED.getStatus()).set(JavaToolkitMethod::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()).set(JavaToolkitMethod::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitMethod::getUpdatedTime, DateTime.now()).in(JavaToolkitMethod::getIdentifier, methodIdentifiers));

                // parameters标记为删除
                javaToolkitParameterService.update(Wrappers.<JavaToolkitParameter>lambdaUpdate().set(JavaToolkitParameter::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()).set(JavaToolkitParameter::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitParameter::getUpdatedTime, DateTime.now()).in(JavaToolkitParameter::getMethodIdentifier, methodIdentifiers));
            }

            // attribute标记为删除
            List<JavaToolkitAttribute> javaToolkitClassAttributes = javaToolkitAttributeService.listByConditions(javaToolkitClass.getIdentifier(), null, null, null, null);
            if (!CollectionUtils.isEmpty(javaToolkitClassAttributes)) {
                List<Long> classAttributeIds = javaToolkitClassAttributes.stream().map(JavaToolkitAttribute::getId).collect(Collectors.toList());
                javaToolkitAttributeService.update(Wrappers.<JavaToolkitAttribute>lambdaUpdate().set(JavaToolkitAttribute::getImportStatus, ImportStatusEnum.NOT_IMPORTED.getStatus()).set(JavaToolkitAttribute::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()).set(JavaToolkitAttribute::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitAttribute::getUpdatedTime, DateTime.now()).in(JavaToolkitAttribute::getId, classAttributeIds));
            }
        }
    }

    /**
     * 添加方法细节
     *
     * @param identifier 入参
     * @return 添加方法细节DTO
     */
    public AddMethodDetailDTO addMethodDetail(String identifier) {
        if (StringUtils.isEmpty(identifier)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class编号不能为空！");
        }
        JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOneByConditions(null, identifier);
        if (Objects.isNull(javaToolkitClass)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class记录不存在！");
        }

        AddMethodDetailDTO detailDTO = new AddMethodDetailDTO();
        // 方法信息
        List<JavaToolkitMethod> javaToolkitMethods = javaToolkitMethodService.listByConditions(javaToolkitClass.getIdentifier(), null, null, ImportStatusEnum.NOT_IMPORTED.getStatus(), DeleteFlagEnum.DELETED.getCode());
        if (!CollectionUtils.isEmpty(javaToolkitMethods)) {
            List<MethodDTO> methodDTOList = new ArrayList<>(javaToolkitMethods.size());
            for (JavaToolkitMethod javaToolkitMethod : javaToolkitMethods) {
                MethodDTO methodDTO = new MethodDTO();
                BeanUtils.copyProperties(javaToolkitMethod, methodDTO);
                // 未删除的参数
                List<JavaToolkitParameter> javaToolkitParameters = javaToolkitParameterService.listByConditions(javaToolkitMethod.getIdentifier(), null, DeleteFlagEnum.USABLE.getCode());
                if (!CollectionUtils.isEmpty(javaToolkitParameters)) {
                    List<ParameterDTO> parameterDTOList = new ArrayList<>(javaToolkitParameters.size());
                    for (JavaToolkitParameter javaToolkitParameter : javaToolkitParameters) {
                        ParameterDTO parameterDTO = new ParameterDTO();
                        BeanUtils.copyProperties(javaToolkitParameter, parameterDTO);
                        parameterDTOList.add(parameterDTO);
                    }
                    methodDTO.setParameters(parameterDTOList);
                } else {
                    methodDTO.setParameters(Collections.emptyList());
                }
                methodDTOList.add(methodDTO);
            }
            detailDTO.setMethods(methodDTOList);
        } else {
            detailDTO.setMethods(Collections.emptyList());
        }

        // 属性信息
        List<JavaToolkitAttribute> javaToolkitAttributes = javaToolkitAttributeService.listByConditions(javaToolkitClass.getIdentifier(), null, null, ImportStatusEnum.NOT_IMPORTED.getStatus(), DeleteFlagEnum.DELETED.getCode());
        if (!CollectionUtils.isEmpty(javaToolkitAttributes)) {
            List<AttributeDTO> attributeDTOList = new ArrayList<>(javaToolkitAttributes.size());
            for (JavaToolkitAttribute javaToolkitAttribute : javaToolkitAttributes) {
                AttributeDTO attributeDTO = new AttributeDTO();
                BeanUtils.copyProperties(javaToolkitAttribute, attributeDTO);
                attributeDTOList.add(attributeDTO);
            }
            detailDTO.setAttributes(attributeDTOList);
        } else {
            detailDTO.setAttributes(Collections.emptyList());
        }
        return detailDTO;
    }

    /**
     * 添加方法
     *
     * @param dto 添加方法DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void addMethodSave(AddMethodDetailDTO dto) {
        if (Objects.isNull(dto)) {
            return;
        }
        // 方法
        List<MethodDTO> methodDTOList = dto.getMethods();
        if (!CollectionUtils.isEmpty(methodDTOList)) {
            for (MethodDTO methodDTO : methodDTOList) {
                JavaToolkitMethod javaToolkitMethod = javaToolkitMethodService.getOneByConditions(null, methodDTO.getIdentifier());
                if (Objects.nonNull(javaToolkitMethod)) {
                    // 更新方法下的参数为可用状态
                    javaToolkitParameterService.update(Wrappers.<JavaToolkitParameter>lambdaUpdate().set(JavaToolkitParameter::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(JavaToolkitParameter::getMethodIdentifier, javaToolkitMethod.getIdentifier()));

                    List<JavaToolkitParameter> javaToolkitParameters = javaToolkitParameterService.listByConditions(javaToolkitMethod.getIdentifier(), null, DeleteFlagEnum.USABLE.getCode());
                    // 方法模板编译
                    JSONObject methodTemplateCompile = commonTemplateBiz.buildJavaToolkitMethodTemplate(javaToolkitMethod, javaToolkitParameters);
                    String compiledMethodTemplate = methodTemplateCompile.toJSONString();
                    log.info("add method save：compileMethodTemplate={}", compiledMethodTemplate);

                    javaToolkitMethodService.update(Wrappers.<JavaToolkitMethod>lambdaUpdate().set(JavaToolkitMethod::getLabel, methodDTO.getLabel()).set(JavaToolkitMethod::getImportStatus, ImportStatusEnum.IMPORTED.getStatus()).set(JavaToolkitMethod::getStatus, EnableDisableEnum.ENABLE.getValue()).set(JavaToolkitMethod::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).set(JavaToolkitMethod::getCompileTemplate, compiledMethodTemplate).set(JavaToolkitMethod::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitMethod::getUpdatedTime, DateTime.now()).eq(JavaToolkitMethod::getId, javaToolkitMethod.getId()));
                }
            }
        }
        // 属性
        List<AttributeDTO> attributeDTOList = dto.getAttributes();
        if (!CollectionUtils.isEmpty(attributeDTOList)) {
            for (AttributeDTO attributeDTO : attributeDTOList) {
                JavaToolkitAttribute javaToolkitAttribute = javaToolkitAttributeService.getOneByConditions(null, attributeDTO.getIdentifier());
                if (Objects.nonNull(javaToolkitAttribute)) {
                    javaToolkitAttributeService.update(Wrappers.<JavaToolkitAttribute>lambdaUpdate().set(JavaToolkitAttribute::getLabel, attributeDTO.getLabel()).set(JavaToolkitAttribute::getImportStatus, ImportStatusEnum.IMPORTED.getStatus()).set(JavaToolkitAttribute::getStatus, EnableDisableEnum.ENABLE.getValue()).set(JavaToolkitAttribute::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).set(JavaToolkitAttribute::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitAttribute::getUpdatedTime, DateTime.now()).eq(JavaToolkitAttribute::getId, javaToolkitAttribute.getId()));
                }
            }
        }
    }

    /**
     * 获取方法详情
     *
     * @param identifier 入参
     * @return 方法详情DTO
     */
    public MethodDetailDTO methodDetail(String identifier) {
        JavaToolkitMethod javaToolkitMethod = javaToolkitMethodService.getOneByConditions(null, identifier);
        if (Objects.isNull(javaToolkitMethod)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "method记录不存在！");
        }
        MethodDetailDTO methodDetailDTO = new MethodDetailDTO();

        // 方法
        MethodDTO methodDTO = new MethodDTO();
        BeanUtils.copyProperties(javaToolkitMethod, methodDTO);
        methodDetailDTO.setMethod(methodDTO);

        // 参数
        List<ParameterDTO> parameters = new ArrayList<>(MagicNumbers.TEN);
        List<JavaToolkitParameter> javaToolkitParameters = javaToolkitParameterService.listByConditions(javaToolkitMethod.getIdentifier(), null, DeleteFlagEnum.USABLE.getCode());
        if (!CollectionUtils.isEmpty(javaToolkitParameters)) {
            for (JavaToolkitParameter javaToolkitParameter : javaToolkitParameters) {
                ParameterDTO parameterDTO = new ParameterDTO();
                BeanUtils.copyProperties(javaToolkitParameter, parameterDTO);
                parameters.add(parameterDTO);
            }
        }
        methodDetailDTO.setParameters(parameters);
        return methodDetailDTO;
    }

    /**
     * 更新方法详情
     *
     * @param dto 方法详情DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateMethod(MethodDetailDTO dto) {
        if (StringUtils.isEmpty(dto.getMethod().getIdentifier())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "method编码不能为空");
        }

        // 方法模板校验
        methodTemplateCheck(dto);

        JavaToolkitMethod javaToolkitMethod = javaToolkitMethodService.getOneByConditions(null, dto.getMethod().getIdentifier());
        if (Objects.isNull(javaToolkitMethod)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "method记录不存在");
        }

        List<JavaToolkitParameter> javaToolkitParameters = javaToolkitParameterService.listByConditions(javaToolkitMethod.getIdentifier(), null, DeleteFlagEnum.USABLE.getCode());

        // 方法模板编译
        javaToolkitMethod.setTemplate(dto.getMethod().getTemplate());
        JSONObject methodTemplateCompile = commonTemplateBiz.buildJavaToolkitMethodTemplate(javaToolkitMethod, javaToolkitParameters);
        String compiledMethodTemplate = methodTemplateCompile.toJSONString();
        log.info("update method：compiledMethodTemplate={}", compiledMethodTemplate);

        // 更新
        javaToolkitMethodService.update(Wrappers.<JavaToolkitMethod>lambdaUpdate().set(JavaToolkitMethod::getLabel, dto.getMethod().getLabel()).set(JavaToolkitMethod::getStatus, dto.getMethod().getStatus()).set(JavaToolkitMethod::getTemplate, dto.getMethod().getTemplate()).set(JavaToolkitMethod::getCompileTemplate, compiledMethodTemplate).set(JavaToolkitMethod::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitMethod::getUpdatedTime, DateTime.now()).eq(JavaToolkitMethod::getId, javaToolkitMethod.getId()));
    }

    /**
     * 删除java工具方法的校验
     *
     * @param dto java工具类编号DTO
     */
    public void deleteMethodCheck(JavaToolkitIdentifierDTO dto) {
        JavaToolkitMethod javaToolkitMethod = javaToolkitMethodService.getOneByConditions(null, dto.getIdentifier());
        if (Objects.isNull(javaToolkitMethod)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "method记录不存在！");
        }
        List<StrComJavaToolkit> strComJavaToolkits = strComJavaToolkitService.list(Wrappers.<StrComJavaToolkit>lambdaQuery().in(StrComJavaToolkit::getMethodIdentifier, javaToolkitMethod.getIdentifier()));
        // 类下的方法是否被策略引用，如果被引用，不允许删除。
        if (CollectionUtils.isEmpty(strComJavaToolkits)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "确认删除java方法？");
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类下的方法被策略引用，不允许删除");
        }
    }

    /**
     * 删除java工具方法
     *
     * @param dto java工具类编号DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMethod(JavaToolkitIdentifierDTO dto) {
        JavaToolkitMethod javaToolkitMethod = javaToolkitMethodService.getOneByConditions(null, dto.getIdentifier());
        if (Objects.isNull(javaToolkitMethod)) {
            return;
        }

        // method标记为删除
        javaToolkitMethodService.update(Wrappers.<JavaToolkitMethod>lambdaUpdate().set(JavaToolkitMethod::getImportStatus, ImportStatusEnum.NOT_IMPORTED.getStatus()).set(JavaToolkitMethod::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()).set(JavaToolkitMethod::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitMethod::getUpdatedTime, DateTime.now()).in(JavaToolkitMethod::getId, javaToolkitMethod.getId()));

        // parameter标记为删除
        javaToolkitParameterService.update(Wrappers.<JavaToolkitParameter>lambdaUpdate().set(JavaToolkitParameter::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()).set(JavaToolkitParameter::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitParameter::getUpdatedTime, DateTime.now()).eq(JavaToolkitParameter::getMethodIdentifier, javaToolkitMethod.getIdentifier()));

    }

    /**
     * 获取属性详情
     *
     * @param identifier 入参
     * @return 属性DTO
     */
    public AttributeDTO attributeDetail(String identifier) {
        JavaToolkitAttribute javaToolkitAttribute = javaToolkitAttributeService.getOneByConditions(null, identifier);
        if (Objects.isNull(javaToolkitAttribute)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "attribute记录不存在！");
        }
        AttributeDTO attributeDTO = new AttributeDTO();
        BeanUtils.copyProperties(javaToolkitAttribute, attributeDTO);
        return attributeDTO;
    }

    /**
     * 更新属性
     *
     * @param dto 属性DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttribute(AttributeDTO dto) {
        if (StringUtils.isEmpty(dto.getIdentifier())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "attribute编码不能为空！");
        }
        JavaToolkitAttribute javaToolkitAttribute = javaToolkitAttributeService.getOneByConditions(null, dto.getIdentifier());
        if (Objects.isNull(javaToolkitAttribute)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "attribute记录不存在！");
        }

        // 更新
        javaToolkitAttributeService.update(Wrappers.<JavaToolkitAttribute>lambdaUpdate().set(JavaToolkitAttribute::getLabel, dto.getLabel()).set(JavaToolkitAttribute::getStatus, dto.getStatus()).set(JavaToolkitAttribute::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitAttribute::getUpdatedTime, DateTime.now()).eq(JavaToolkitAttribute::getId, javaToolkitAttribute.getId()));

        JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOneByConditions(null, javaToolkitAttribute.getClassIdentifier());
        if (Objects.isNull(javaToolkitClass)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class记录不存在！");
        }

        // 更新class的JsonSchema
        updateClassJsonSchema(javaToolkitClass);
    }

    /**
     * 获取有效的 Java工具方法
     *
     * @param wrlType 入参
     * @return java工具类-method
     */
    public List<JavaToolkitMethod> getValidJavaToolkitMethods(String wrlType) {
        DataVariableBasicTypeEnum basicTypeEnum = DataVariableBasicTypeEnum.getNameEnum(wrlType);
        return Optional.ofNullable(javaToolkitMethodService.list(Wrappers.<JavaToolkitMethod>lambdaQuery().eq(JavaToolkitMethod::getStatus, EnableDisableEnum.ENABLE.getValue()).eq(JavaToolkitMethod::getImportStatus, ImportStatusEnum.IMPORTED.getStatus()).eq(JavaToolkitMethod::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(Objects.nonNull(basicTypeEnum), JavaToolkitMethod::getReturnValueWrlType, wrlType))).orElse(Collections.emptyList());
    }

    /**
     * 通过ExistingJavaType获取JsonSchema类
     *
     * @param canonicalName 入参
     * @return Java工具属性的jsonschema
     */
    public String getClassJsonSchemaByExistingJavaType(String canonicalName) {
        if (StringUtils.isEmpty(canonicalName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类名全路径不能为空！");
        }
        JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOneByCanonicalName(canonicalName);
        if (Objects.isNull(javaToolkitClass)) {
            return StringPool.EMPTY;
        }
        return javaToolkitClass.getAttributeJsonSchema();
    }

    /**
     * class删除的时候是否触发jar包删除
     *
     * @param javaToolkitClass
     * @return true:删除jar包，false：不删除jar包
     */
    private boolean deleteJarCheck(JavaToolkitClass javaToolkitClass) {
        if (Objects.isNull(javaToolkitClass)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "class记录不存在！");
        }
        // jar包下除了当前class之外没有可用的class时，移除jar
        List<JavaToolkitClass> javaToolkitClasses = javaToolkitClassService.list(Wrappers.<JavaToolkitClass>lambdaQuery().select(JavaToolkitClass::getId).eq(JavaToolkitClass::getJarIdentifier, javaToolkitClass.getJarIdentifier()).eq(JavaToolkitClass::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).ne(JavaToolkitClass::getIdentifier, javaToolkitClass.getIdentifier()));
        return CollectionUtils.isEmpty(javaToolkitClasses);
    }

    /**
     * 封装对象的jsonSchema
     *
     * @param javaToolkitClass
     * @param javaToolkitAttributes
     * @return 封装后的jsonSchema
     */
    private String javaObjectJsonSchemaWrapper(JavaToolkitClass javaToolkitClass, List<JavaToolkitAttribute> javaToolkitAttributes) {
        if (Objects.isNull(javaToolkitClass)) {
            return null;
        }

        // 构建java对象根节点
        DomainModelTree rootNode = buildJavaObjectClassRootNode(javaToolkitClass.getName(), javaToolkitClass.getCanonicalName(), javaToolkitClass.getLabel());

        // 构建java对象属性节点列表
        List<DomainModelTree> attributeNodes = buildJavaObjectAttributeNodes(javaToolkitAttributes);
        if (CollectionUtils.isEmpty(attributeNodes)) {
            rootNode.setChildren(Collections.emptyList());
        } else {
            rootNode.setChildren(attributeNodes);
        }

        JSONObject jsonObject = DomainModelTreeUtils.domainModelTreeConvertJsonObject(rootNode);
        return jsonObject.toJSONString();
    }

    /**
     * 构建java对象根节点
     *
     * @param className
     * @param canonicalName
     * @param label
     * @return 决策领域树形结构实体
     */
    private DomainModelTree buildJavaObjectClassRootNode(String className, String canonicalName, String label) {
        DomainModelTree classRootNode = new DomainModelTree();
        classRootNode.setName(className);
        classRootNode.setLabel(label);
        classRootNode.setDescribe(label);
        classRootNode.setObjectType(ObjectTypeEnum.REF.getType());
        classRootNode.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        classRootNode.setClassName(className);
        classRootNode.setExistingJavaType(canonicalName);
        classRootNode.setIsRefRootNode(YesNoEnum.YES.getStrValue());
        return classRootNode;
    }

    /**
     * 构建java对象属性节点
     *
     * @param javaToolkitClass
     * @param javaToolkitAttribute
     * @return 决策领域树形结构实体
     */
    private DomainModelTree buildJavaObjectClassAttributeNode(JavaToolkitClass javaToolkitClass, JavaToolkitAttribute javaToolkitAttribute) {
        DomainModelTree classAttributeNode = new DomainModelTree();
        classAttributeNode.setObjectType(ObjectTypeEnum.REF.getType());
        classAttributeNode.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        if (YesNoEnum.YES.getValue().equals(javaToolkitAttribute.getTypeIsArray())) {
            classAttributeNode.setIsArr(YesNoEnum.YES.getStrValue());
        } else {
            classAttributeNode.setIsArr(YesNoEnum.NO.getStrValue());
        }
        classAttributeNode.setName(javaToolkitAttribute.getName());
        classAttributeNode.setLabel(javaToolkitAttribute.getLabel());
        classAttributeNode.setDescribe(javaToolkitAttribute.getLabel());
        classAttributeNode.setClassName(javaToolkitClass.getName());
        classAttributeNode.setExistingJavaType(javaToolkitClass.getCanonicalName());
        classAttributeNode.setAccess(javaToolkitAttribute.getAccess());
        classAttributeNode.setSourceType(javaToolkitAttribute.getSourceType());
        classAttributeNode.setIsRefRootNode(YesNoEnum.NO.getStrValue());
        classAttributeNode.setAttributeIdentifier(javaToolkitAttribute.getIdentifier());
        return classAttributeNode;
    }

    /**
     * 构建java对象属性节点列表
     *
     * @param javaToolkitAttributes
     * @return 决策领域树形结构实体 列表
     */
    private List<DomainModelTree> buildJavaObjectAttributeNodes(List<JavaToolkitAttribute> javaToolkitAttributes) {
        if (CollectionUtils.isEmpty(javaToolkitAttributes)) {
            return Collections.emptyList();
        }

        // 过滤出静态的属性
        List<JavaToolkitAttribute> staticJavaToolkitAttributes = javaToolkitAttributes.stream().filter(item -> Modifier.isStatic(item.getModifier())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(staticJavaToolkitAttributes)) {
            for (JavaToolkitAttribute javaToolkitAttribute : staticJavaToolkitAttributes) {
                // 移除私有的静态属性
                if (Modifier.isPrivate(javaToolkitAttribute.getModifier())) {
                    javaToolkitAttributes.remove(javaToolkitAttribute);
                }
            }
        }

        List<DomainModelTree> attributeNodes = new ArrayList<>(MagicNumbers.TEN);
        for (JavaToolkitAttribute javaToolkitAttribute : javaToolkitAttributes) {
            DomainModelTree attributeNode = buildJavaObjectAttributeNode(javaToolkitAttribute);
            if (Objects.nonNull(attributeNode)) {
                attributeNodes.add(attributeNode);
            }
        }
        return attributeNodes;
    }

    /**
     * 构建java对象属性节点
     *
     * @param javaToolkitAttribute
     * @return 决策领域树形结构实体
     */
    private DomainModelTree buildJavaObjectAttributeNode(JavaToolkitAttribute javaToolkitAttribute) {
        if (Objects.isNull(javaToolkitAttribute)) {
            return null;
        }
        if (DataVariableSimpleTypeEnum.getMessageEnum(javaToolkitAttribute.getWrlType()) == null) {
            // 启用状态 & 已导入 & 可用
            JavaToolkitClass javaToolkitClass = javaToolkitClassService.getOne(Wrappers.<JavaToolkitClass>lambdaQuery().select(JavaToolkitClass::getIdentifier, JavaToolkitClass::getName, JavaToolkitClass::getCanonicalName).eq(JavaToolkitClass::getCanonicalName, javaToolkitAttribute.getWrlType()).eq(JavaToolkitClass::getStatus, EnableDisableEnum.ENABLE.getValue()).eq(JavaToolkitClass::getImportStatus, ImportStatusEnum.IMPORTED.getStatus()).eq(JavaToolkitClass::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            if (Objects.isNull(javaToolkitClass)) {
                return null;
            } else {
                DomainModelTree classAttributeNode = buildJavaObjectClassAttributeNode(javaToolkitClass, javaToolkitAttribute);
                // 启用状态 & 已导入 & 可用
                List<JavaToolkitAttribute> javaToolkitAttributes = javaToolkitAttributeService.listByConditions(javaToolkitClass.getIdentifier(), null, EnableDisableEnum.ENABLE.getValue(), ImportStatusEnum.IMPORTED.getStatus(), DeleteFlagEnum.USABLE.getCode());
                if (CollectionUtils.isEmpty(javaToolkitAttributes)) {
                    classAttributeNode.setChildren(Collections.emptyList());
                } else {
                    List<DomainModelTree> attributeNodes = buildJavaObjectAttributeNodes(javaToolkitAttributes);
                    classAttributeNode.setChildren(attributeNodes);
                }
                return classAttributeNode;
            }
        } else {
            DomainModelTree attributeNode = new DomainModelTree();
            attributeNode.setName(javaToolkitAttribute.getName());
            attributeNode.setType(javaToolkitAttribute.getWrlType());
            attributeNode.setIsArr(YesNoEnum.YES.getValue().equals(javaToolkitAttribute.getTypeIsArray()) ? YesNoEnum.YES.getStrValue() : YesNoEnum.NO.getStrValue());
            attributeNode.setDescribe(javaToolkitAttribute.getLabel());
            attributeNode.setAccess(javaToolkitAttribute.getAccess());
            attributeNode.setSourceType(javaToolkitAttribute.getSourceType());
            attributeNode.setAttributeIdentifier(javaToolkitAttribute.getIdentifier());
            return attributeNode;
        }
    }

    /**
     * 更新class的JsonSchema
     *
     * @param javaToolkitClass
     */
    private void updateClassJsonSchema(JavaToolkitClass javaToolkitClass) {
        // 封装class对应的jsonSchema时使用到的属性列表
        List<JavaToolkitAttribute> javaToolkitAttributes = javaToolkitAttributeService.listByConditions(javaToolkitClass.getIdentifier(), null, EnableDisableEnum.ENABLE.getValue(), ImportStatusEnum.IMPORTED.getStatus(), DeleteFlagEnum.USABLE.getCode());
        // 封装对象的jsonSchema
        String jsonSchema = javaObjectJsonSchemaWrapper(javaToolkitClass, javaToolkitAttributes);
        javaToolkitClassService.update(Wrappers.<JavaToolkitClass>lambdaUpdate().set(JavaToolkitClass::getAttributeJsonSchema, jsonSchema).set(JavaToolkitClass::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(JavaToolkitClass::getUpdatedTime, DateTime.now()).eq(JavaToolkitClass::getId, javaToolkitClass.getId()));
    }
}
