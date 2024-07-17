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
package com.wiseco.var.process.engine.compiler;

import cn.hutool.core.lang.Pair;
import com.wiseco.boot.commons.exception.ServiceException;
import com.wiseco.decision.engine.base.Field;
import com.wiseco.decision.engine.java.common.enums.CompileEnvEnum;
import com.wiseco.decision.engine.java.common.enums.EngineComponentInvokeMetaTypeEnum;
import com.wiseco.decision.engine.java.template.parser.context.content.SyntaxInfo;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.runtime.VarContainer;
import com.wiseco.decision.engine.var.runtime.core.Engine;
import com.wiseco.decision.engine.var.runtime.core.Server;
import com.wiseco.decision.engine.var.runtime.core.Service;
import com.wiseco.decision.engine.var.transform.component.api.IVarDataProvider;
import com.wiseco.decision.engine.var.transform.component.compiler.IVarCompilerEntry;
import com.wiseco.decision.engine.var.transform.component.compiler.VarCompileResult;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.decision.engine.var.transform.component.data.VarFunctionCompileQueryResultDto;
import com.wiseco.decision.engine.var.transform.enums.DataValuePrefixEnum;
import com.wiseco.decision.engine.var.utils.ComponentClassNameGenerator;
import com.wiseco.decision.engine.var.utils.PackageGenerator;
import com.wiseco.decision.model.engine.VarProcessSpace;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.service.engine.IVarProcessCompileVarProvider;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.openhft.compiler.CompilerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 单变量测试入口
 * @author wiseco
 *
 */
@Slf4j
@Component
public class VarEngineDelegator {
    @Value("${system.compile.savePath}")
    private String    clsSavePath;

    @Value("${system.compile.libJarPath:}")
    private String    libJarPath;

    @Value("${system.compile.attrSplitThreshold:50}")
    private Integer attrSplitThreshold;

    @Autowired
    IVarCompilerEntry varCompiler;

    @Autowired
    ServiceExporter   serviceExporter;
    @Autowired
    IVarDataProvider  varProvider;
    @Autowired
    IVarProcessCompileVarProvider varProcessCompileVarProvider;

    @PostConstruct
    private void initCustomClassPath() {
        addClassPath(libJarPath);
    }

    private void addClassPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            String[] fileList = file.list();
            if (fileList != null && fileList.length != 0) {
                for (String filePath : fileList) {
                    CompilerUtils.addClassPath(path + File.separator + filePath);
                }
            }
        }
    }

    /**
     * 初始化engine容器，用于变量和公共函数的场景
     * @param varSpace 变量空间信息
     * @param compileData 组件编译的数据
     * @return engine容器
     * @throws Throwable
     */
    public Engine initServiceEngine(VarProcessSpace varSpace, VarCompileData compileData)
                                                                                         throws Throwable {
        String classPath = new File(clsSavePath).toPath().toString();
        //初始化容器 
        VarContainer decision = new VarContainer(classPath, libJarPath, false);
        return initServiceEngine(decision, varSpace, compileData);
    }

    /**
     * 初始化公共函数或变量的engine容器
     * @param decision decision
     * @param varSpace 变量空间
     * @param compileData 组件编译的数据
     * @return engine容器
     * @throws Throwable 抛出异常
     */
    public Engine initServiceEngine(VarContainer decision, VarProcessSpace varSpace,
                                    VarCompileData compileData) throws Throwable {
        long time1 = System.currentTimeMillis();
        log.info("变量测试，容器准备开始:varId:{},varName:{} identifier:{},changeNum:{} ",
            compileData.getVarId(), compileData.getName(), compileData.getIdentifier(),
            compileData.getChangeNum());
        String spaceCode = varSpace.getCode();
        //容器准备
        Engine mainEngine = getEngine(decision, varSpace);
        Map<String, JSONObject> templateConfig = varProvider.getTemplateConfig(varSpace.getId(), compileData.getType());
        log.info("变量测试，变量代码生成开始:varId:{},varName:{} identifier:{},changeNum:{} ",
            compileData.getVarId(), compileData.getName(), compileData.getIdentifier(), compileData.getChangeNum());
        //编译分析用到变量,只生成java代码不编译cls
        VarCompileResult analyzeResult = varCompiler.compile(compileData, templateConfig, true,
            mainEngine, CompileEnvEnum.TEST);
        long time2 = System.currentTimeMillis();
        if (!analyzeResult.isSuccess()) {
            throw new ServiceException("变量代码生成失败");
        }
        Map<String, String> scriptMap = analyzeResult.getScriptMap();
        log.info("单个变量测试生成的代码:{}", scriptMap);
        //根据直接引用callInfo信息查询到间接引用数据
        VarFunctionCompileQueryResultDto queryResult = findCallInfoSet(varSpace.getId(),analyzeResult.getSyntaxInfo().getCallInfo());
        // key 是以.java 结尾的全路径名称按需生成并编译rawData并标记运行时装载
        log.info("变量测试，模型变量生成开始:varId:{},varName:{} identifier:{},changeNum:{} ",
                compileData.getVarId(), compileData.getName(), compileData.getIdentifier(), compileData.getChangeNum());
        Map<String, String> javaCodeMap = new LinkedHashMap<>();
        javaCodeMap.putAll(generateCompileRawDataMark(spaceCode, varSpace.getInputData(), mainEngine
            .getService().getServer().getDecision(), queryResult, attrSplitThreshold));
        javaCodeMap.putAll(generateCompileVarsDataMark(spaceCode, mainEngine, queryResult,compileData));

        String packageName = MessageFormat.format("{0}.{1}", PackageGenerator.ENGINE_PACKAGE, MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, compileData.getIdentifier()));
        String varClsName = MessageFormat.format("{0}.{1}.java", packageName, MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, compileData.getIdentifier()));
        String parameterClsName = MessageFormat.format("{0}.parameters.java", packageName);
        String localVarsClsName = MessageFormat.format("{0}.localVars.java", packageName);
        if (scriptMap.containsKey(varClsName)) {
            javaCodeMap.put(varClsName, scriptMap.get(varClsName));
        }
        if (scriptMap.containsKey(parameterClsName)) {
            javaCodeMap.put(parameterClsName, scriptMap.get(parameterClsName));
        }
        if (scriptMap.containsKey(localVarsClsName)) {
            javaCodeMap.put(localVarsClsName, scriptMap.get(localVarsClsName));
        }

        //补充一下运行时需要涉及的其它公共函数,因为有可能是未审核的测试，所以后端可能无法提前预判，需要根据引擎分析结果实时查询
        Map<String, byte[]> dependsCls = new HashMap<>(MagicNumbers.INT_64);
        List<SyntaxInfo.CallInfo> callInfoList = analyzeResult.getSyntaxInfo().getCallInfo().stream()
                .filter(e -> EngineComponentInvokeMetaTypeEnum.COMPONENT == e.getType())
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(callInfoList)) {
            log.info("变量测试，补充其它变量class开始:varId:{},varName:{} identifier:{},changeNum:{} ",
                compileData.getVarId(), compileData.getName(), compileData.getIdentifier(), compileData.getChangeNum());
            List<String> javaClsList = queryResult.getJavaClsList();
            for (String javaCls : javaClsList) {
                Map<String,String> compiledClsMap = JSON.parseObject(javaCls, Map.class);
                compiledClsMap.entrySet().forEach(entry -> {
                    dependsCls.put(entry.getKey(), Base64.getDecoder().decode(entry.getValue()));
                });
            }
        }
        long time3 = System.currentTimeMillis();
        log.info("变量测试，代码编译开始:varId:{},varName:{} identifier:{},changeNum:{} ",
                compileData.getVarId(), compileData.getName(), compileData.getIdentifier(), compileData.getChangeNum());
        // key 是不以.java 结尾的全路径名称
        Map<String, byte[]> varBytes = compile(decision, spaceCode, javaCodeMap, dependsCls);
        varBytes.putAll(dependsCls);
        long time4 = System.currentTimeMillis();
        //装载运行时引擎所需组件
        log.info("变量测试，load变量开始:varId:{},varName:{} identifier:{},changeNum:{} ",
            compileData.getVarId(), compileData.getName(), compileData.getIdentifier(), compileData.getChangeNum());
        loadEngineComponents(mainEngine, compileData, mainEngine.loadClassMap(varBytes), queryResult);
        long time5 = System.currentTimeMillis();
        log.info(
            "变量测试，容器准备结束:varId:{} varName:{} identifier:{},version:{} 结束，总耗时:{}ms 变量代码生成耗时:{}ms 其它代码生成耗时:{}ms 编译耗时:{} ms，填充其它class:{} ms,load:{}ms",
            compileData.getVarId(), compileData.getName(), compileData.getIdentifier(),
            compileData.getChangeNum(), System.currentTimeMillis() - time1, time2 - time1, time3 - time2,
            time4 - time3, time5 - time4, System.currentTimeMillis() - time5);
        return mainEngine;
    }
    /**
     * 查询某个组件的穿透引用信息
     * @param spaceId 空间id
     * @param entryComponentCallInfo 直接引用信息
     * @return 直接引用包括间接引用组件变量信息
     */
    private VarFunctionCompileQueryResultDto findCallInfoSet(Long spaceId, List<SyntaxInfo.CallInfo> entryComponentCallInfo) {
        // 引用的组件信息
        Set<String> varComponentSet = new HashSet<>();
        Set<String> functionComponentSet = new HashSet<>();
        // 引用的变量信息
        Set<String> varPathSet = new HashSet<>();
        // 引用的参数和本地变量的模型类型信息
        Set<String> modelTypeSet = new HashSet<>();
        // 依赖组件class信息
        List<String> javaClsList = new ArrayList<>();

        for (SyntaxInfo.CallInfo callInfo : entryComponentCallInfo) {
            VarTypeEnum varType = VarTypeEnum.FUNCTION;
            if (callInfo instanceof VarSyntaxInfo.VarCallInfo) {
                varType = ((VarSyntaxInfo.VarCallInfo) callInfo).getVarType();
            }
            if (EngineComponentInvokeMetaTypeEnum.VAR == callInfo.getType()) {
                varPathSet.add(callInfo.getValue());
            } else if (EngineComponentInvokeMetaTypeEnum.TYPE == callInfo.getType()) {
                modelTypeSet.add(callInfo.getValue());
            } else {
                //否则是非变量的组件
                if (varType == VarTypeEnum.VAR && !varComponentSet.contains(callInfo.getValue())) {
                    varComponentSet.add(callInfo.getValue());
                    VarFunctionCompileQueryResultDto resultDto = varProcessCompileVarProvider.getAllIdentifiersAndVarPathes(spaceId, null, varType, callInfo.getValue(),false);
                    varComponentSet.addAll(resultDto.getVarComponentSet());
                    functionComponentSet.addAll(resultDto.getFunctionComponentSet());
                    varPathSet.addAll(resultDto.getVarPathSet());
                    modelTypeSet.addAll(resultDto.getModelTypeSet());
                    javaClsList.addAll(resultDto.getJavaClsList());
                } else if (varType == VarTypeEnum.FUNCTION && !functionComponentSet.contains(callInfo.getValue())) {
                    functionComponentSet.add(callInfo.getValue());
                    VarFunctionCompileQueryResultDto resultDto = varProcessCompileVarProvider.getAllIdentifiersAndVarPathes(spaceId, null, varType, callInfo.getValue(),false);
                    varComponentSet.addAll(resultDto.getVarComponentSet());
                    functionComponentSet.addAll(resultDto.getFunctionComponentSet());
                    varPathSet.addAll(resultDto.getVarPathSet());
                    modelTypeSet.addAll(resultDto.getModelTypeSet());
                    javaClsList.addAll(resultDto.getJavaClsList());
                }
            }
        }
        return new VarFunctionCompileQueryResultDto(varComponentSet, functionComponentSet, varPathSet, modelTypeSet, javaClsList);
    }

    /**
     * 组装依赖函数的class文件内容
     * @param spaceId 空间id
     * @param interfaceId 接口id
     * @param callInfoList 直接引用信息
     * @param varBytes 依赖类字节码信息
     * @return 依赖类字节码信息
     */
    private Map<String, byte[]> fillFunctionCls(Long spaceId,Long interfaceId, List<SyntaxInfo.CallInfo> callInfoList,Map<String, byte[]> varBytes) {
		//反向查询
        callInfoList.forEach(data -> {
            VarTypeEnum varTypeEnum = VarTypeEnum.FUNCTION;
            if (data instanceof VarSyntaxInfo.VarCallInfo) {
                VarSyntaxInfo.VarCallInfo callInfo = (VarSyntaxInfo.VarCallInfo) data;
                varTypeEnum = callInfo.getVarType();
            }
            putClsInfo(spaceId, interfaceId, varTypeEnum, data.getValue(), varBytes);

		});
        return varBytes;
	}

    /**
     * 组装依赖函数的class文件内容
     * @param spaceId 空间id
     * @param interfaceId 接口id
     * @param varType 变量组件类型
     * @param identifierLst identifier集合
     * @param dependsCls 依赖字节码信息
     * @return 依赖类字节码信息
     */
    private Map<String, byte[]> fillFunctionClsByIdentifierSet(Long spaceId, Long interfaceId, VarTypeEnum varType, Set<String> identifierLst, Map<String, byte[]> dependsCls) {
        //反向查询
        identifierLst.forEach(data -> {
            putClsInfo(spaceId, interfaceId, varType, data, dependsCls);

        });
        return dependsCls;
    }

    /**
     * 查询identifier的组件信息
     * @param spaceId
     * @param interfaceId
     * @param varType
     * @param identifier
     * @param varBytes
     */
    private void putClsInfo(Long spaceId, Long interfaceId, VarTypeEnum varType, String identifier, Map<String, byte[]> varBytes) {
        VarCompileData compileData = null;
        try {
            compileData = varProvider.getCheckedInVarData(spaceId, interfaceId, varType, identifier);
        } catch (Exception e) {
            log.warn(MessageFormat.format("查询公共函数的定义信息失败【interfaceId:{0} identifier={1}】", interfaceId + "",identifier),e);
            throw new ServiceException(MessageFormat.format("查询公共函数的定义信息失败【interfaceId:{0} identifier={1}】,error:{2}", interfaceId + "",identifier,e));
        }

        Map<String,String> compiledClsMap = JSON.parseObject(compileData.getJavaCls(), Map.class);
        compiledClsMap.entrySet().forEach(entry -> {
            varBytes.put(entry.getKey(), Base64.getDecoder().decode(entry.getValue()));
        });
    }

    private  void loadEngineComponents(Engine mainEngine, VarCompileData flowData, Map<String,Class<?>> classMap, VarFunctionCompileQueryResultDto queryResult) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		// 初始化input
		if (mainEngine.getService().getServer().isInput()) {
			mainEngine.setRawDataClass(classMap.get(MessageFormat.format("{0}.rawData", PackageGenerator.ENGINE_PACKAGE)));
		}
		if (mainEngine.getService().getServer().isVars()) {
			mainEngine.setVarsClass(classMap.get(MessageFormat.format("{0}.vars", PackageGenerator.ENGINE_PACKAGE)));
		}
		// 初始化表达式依赖的其它运行所需要的组件, 包含变量\公共函数的数据模型对应的预处理组件列表
		Set<String> varIdentifierLst = queryResult.getVarComponentSet();
		Set<String> functionIdentifierLst = queryResult.getFunctionComponentSet();
		varIdentifierLst.forEach(identifier -> {
			try {
				mainEngine.addVar(MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, identifier),classMap.get(MessageFormat.format("{0}.{1}.{1}", PackageGenerator.ENGINE_PACKAGE,MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, identifier))));
			} catch (Exception e) {
				log.warn("load ref component failed, id: {}, id list: {}", identifier, StringUtils.collectionToCommaDelimitedString(varIdentifierLst), e);
			}
		});
        functionIdentifierLst.forEach(identifier -> {
            try {
                mainEngine.addVar(MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, identifier),classMap.get(MessageFormat.format("{0}.{1}.{1}", PackageGenerator.ENGINE_PACKAGE,MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, identifier))));
            } catch (Exception e) {
                log.warn("load ref component failed, id: {}, id list: {}", identifier, StringUtils.collectionToCommaDelimitedString(varIdentifierLst), e);
            }
        });
		//入口决策流变量，identifer后端自动生成一个，服务发布的时候也用这个
		mainEngine.addEntryVarName((MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, flowData.getIdentifier())));
		mainEngine.addVar(MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, flowData.getIdentifier()),classMap.get(MessageFormat.format("{0}.{1}.{1}", PackageGenerator.ENGINE_PACKAGE,MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, flowData.getIdentifier()))));
	}

    private static Engine getEngine(VarContainer decision, VarProcessSpace varSpace) {
        Server server = decision.addServer(varSpace.getCode());
        server.setSpace(varSpace);

        Service serviceContainer = server.addService(varSpace.getCode());
        Engine mainEngine = serviceContainer.addEngine(varSpace.getCode());
        mainEngine.setVarInterface(null);
        return mainEngine;
    }

    private static Map<String,String> generateCompileRawDataMark(String spaceCode, String inputJsonSchema, VarContainer decision, VarFunctionCompileQueryResultDto queryResult, int attrSplitThreshold) throws Throwable {
		if (StringUtils.hasText(inputJsonSchema)) {
			Set<String> varSet = new HashSet<>();
			if (queryResult.getVarPathSet() != null) {
                queryResult.getVarPathSet().forEach((varName) -> {
                    if (varName.toLowerCase().startsWith(DataValuePrefixEnum.RAWDATA.name().toLowerCase())) {
                        varSet.add(varName);
                    }
                });
            }
            if (queryResult.getModelTypeSet() != null) {
                queryResult.getModelTypeSet().forEach((varName) -> {
                    if (varName.toLowerCase().startsWith(DataValuePrefixEnum.RAWDATA.name().toLowerCase())) {
                        varSet.add(varName);
                    }
                });
            }
			//编译input
			Server server = decision.getServer(spaceCode);
			server.setInput(true);
			Map<String, List<Pair<String, String>>> inputMap = new HashMap<>(MagicNumbers.INT_64);
			inputMap.put(PackageGenerator.ENGINE_PACKAGE, Collections.singletonList(new Pair<>("rawData", inputJsonSchema)));
			log.info("变量测试rawData代码生成 spaceCode:{}  varSet:{}",spaceCode,varSet);
			Map<String,String> codeMap =  decision.generateEngineClassByPropertyPathsAndPropertyPrefix(spaceCode, spaceCode,spaceCode, varSet, inputMap,"rawData", attrSplitThreshold);
			log.info("变量测试rawData代码生成 spaceCode:{}  codeMap:{}",spaceCode,codeMap);
			return codeMap;
		}
		return new HashMap<>(MagicNumbers.INT_1);
	}

    private static Map<String,String> generateCompileVarsDataMark(String spaceCode,Engine mainEngine,VarFunctionCompileQueryResultDto queryResult,VarCompileData compileData) throws Throwable {
		Set<String> varSet = new HashSet<>();
		List<Field> varsFieldLst = new ArrayList<>();

        if (queryResult.getVarPathSet() != null) {
            queryResult.getVarPathSet().forEach((varName) -> {
                if (varName.toLowerCase().startsWith(DataValuePrefixEnum.VARS.name().toLowerCase()) && !varSet.contains(varName)) {
                    varSet.add(varName);
                    String realVarName = (varName.split("\\."))[1];
                    //获取变量定义得集合
                    Assert.notEmpty(compileData.getVarContents(),"动态生存变量清单列表，后端数据为空！");
                    //获取调用变量得wrl数据类型
                    String realType = compileData.getVarContents().stream().filter(varContent -> realVarName.equalsIgnoreCase(varContent.getEnName())).findFirst().get().getReturnType();
                    //拼接生产jsonschema得对象信息
                    varsFieldLst.add(new Field(realVarName, realType, false));
                }
            });
        }
		//即使是不引用任何其它变量，也要生成vars类
		mainEngine.getService().getServer().setVars(true);
		Map parameterMap = new HashMap(MagicNumbers.EIGHT);
		parameterMap.put(MessageFormat.format("{0}", PackageGenerator.ENGINE_PACKAGE), Collections.singletonList(new Pair<>("vars", VarContainer.generateJsonSchemaByFields("vars", varsFieldLst))));
		log.info("变量测试vars代码生成 spaceCode:{}  parameterMap:{}",spaceCode,parameterMap);
		//只生成java代码
		Map<String,String> varCode = mainEngine.getService().getServer().getDecision().generateEngineClassByJsonSchema(spaceCode,spaceCode, spaceCode, parameterMap);
		log.info("变量测试vars代码生成 spaceCode:{}  varCode:{}",spaceCode,varCode);

		return varCode;
	}

    private Map<String, byte[]> compile(VarContainer decision, String spaceCode, Map<String, String> codeMap,
                                        Map<String, byte[]> dependsCls) {
        log.debug("单个变量测试，代码编译，spaceCode:{} codeMap:{}", spaceCode, codeMap);
        try {
            return decision.compileEngineClassByJavaCode(spaceCode, spaceCode, spaceCode,
                codeMap, dependsCls);
        } catch (ServiceException e) {
            log.warn("compile var failed", e);
            throw e;
        } catch (Throwable e) {
            log.warn("compile var failed", e);
            throw new ServiceException("编译失败:" + e.getMessage());
        }
    }
}
