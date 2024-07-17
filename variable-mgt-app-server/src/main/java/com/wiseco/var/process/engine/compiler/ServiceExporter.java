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
import com.wiseco.decision.engine.java.component.IComponent;
import com.wiseco.decision.engine.java.template.parser.context.content.SyntaxInfo;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.runtime.VarContainer;
import com.wiseco.decision.engine.var.runtime.api.IVarProcessInterfaceProvider;
import com.wiseco.decision.engine.var.runtime.api.IVarProcessServiceProvider;
import com.wiseco.decision.engine.var.runtime.api.IVarProcessSpaceProvider;
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
import com.wiseco.decision.model.engine.VarDto;
import com.wiseco.decision.model.engine.VarProcessInterface;
import com.wiseco.decision.model.engine.VarProcessService;
import com.wiseco.decision.model.engine.VarProcessSpace;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.service.engine.IVarProcessCompileVarProvider;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 服务测试和服务打包实现
 * @author wiseco
 *
 */
@Component
@Slf4j
public class ServiceExporter {

    @Value("${system.compile.exportPath}")
    private String               clsSavePath;
    @Value("${system.compile.libJarPath:}")
    private String               libJarPath;

    @Value("${system.compile.attrSplitThreshold:50}")
    private Integer attrSplitThreshold;

    @Autowired
    IVarProcessSpaceProvider     spaceProvider;
    @Autowired
    IVarProcessServiceProvider   serviceProvider;
    @Autowired
    IVarProcessInterfaceProvider interfaceProvider;

    @Autowired
    IVarCompilerEntry            varCompiler;
    @Autowired
    IVarDataProvider             varProvider;
    @Autowired
    IVarProcessCompileVarProvider varProcessCompileVarProvider;
    /**
     * 导出变量服务接口压缩包
     * @param interfaceId 变量服务接口ID
     * @param varDefSet 引用变量集合
     * @param varMap 变量服务接口ID
     * @return File 压缩包文件
     * @throws Throwable 异常
     */
    public byte[] exportService(Long interfaceId, Set<VarDto> varDefSet, Map<String, IComponent> varMap) throws Throwable {
        VarProcessInterface processInterface = interfaceProvider.getInterface(interfaceId);
        VarProcessSpace space = spaceProvider.getSpace(processInterface.getVarProcessSpaceId());
        VarProcessService processService = serviceProvider.getService(processInterface.getId());
        long startTime = System.currentTimeMillis();
        byte[] zipFile = compressEngineClassFiles(space, processService, processInterface,
            CompileEnvEnum.PUBLISH, false, varDefSet, varMap);
        log.info("空间：{}的服务：{}下接口：{}打包完成,耗时:{}ms", space.getCode(), processService.getCode(),
            processInterface.getId(), System.currentTimeMillis() - startTime);
        return zipFile;
    }

    /**
     * 获取服务接口维度的引擎
     * 服务测试的获取
     * @param interfaceId 变量服务接口ID
     * @param varDefSet 引用变量信息集合
     * @return engine容器
     * @throws Throwable 异常
     */
    public Engine getServiceInterfaceEngine(Long interfaceId, Set<VarDto> varDefSet)
                                                                                    throws Throwable {
        VarProcessInterface processInterface = interfaceProvider.getInterface(interfaceId);
        VarProcessSpace space = spaceProvider.getSpace(processInterface.getVarProcessSpaceId());
        VarProcessService processService = serviceProvider.getService(processInterface.getId());
        VarContainer decision = generateCompileBuildRuntimeContainer(space, processService,
            processInterface, CompileEnvEnum.TEST, true, varDefSet);

        return decision.getServer(space.getCode()).getEngine(processService.getCode(),
            processInterface.getId().toString());
    }

    /**
     * 代码生成编译并构建变量加工的服务接口容器
     * @param space 空间
     * @param processService 服务
     * @param processInterface 接口
     * @param compileEnv 编译环境信息
     * @param loadCls 是否加载class
     * @param varDefSet 引用变量信息集合
     * @param componentMap 组件信息
     * @return 压缩字节码
     * @throws Throwable 异常
     */
    private byte[] compressEngineClassFiles(VarProcessSpace space, VarProcessService processService,
                                            VarProcessInterface processInterface, CompileEnvEnum compileEnv, Boolean loadCls,
                                            Set<VarDto> varDefSet, Map<String, IComponent> componentMap) throws Throwable {
        Long interfaceId = processInterface.getId();
        String spaceCode = space.getCode();
        String serviceCode = processService.getCode();

        Engine mainEngine = generateCompileBuildRuntimeContainer(space, processService,
            processInterface);
        log.info("变量服务打包，数据准备开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode,
            serviceCode, interfaceId);
        long time1 = System.currentTimeMillis();
        VarCompileData flowData = varProvider.getInterfaceFlowData(interfaceId);
        Map<String, JSONObject> templateConfig = varProvider.getTemplateConfig(space.getId(), null);
        long time2 = System.currentTimeMillis();
        //补充数据
        flowData.setInterfaceId(interfaceId);
        Assert.notNull(flowData, "未查询到服务的变量信息");

        log.info("变量服务打包，变量代码生成开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode,
            serviceCode, interfaceId);
        VarCompileResult analyzeResult = varCompiler.compile(flowData, templateConfig, true,
            mainEngine, compileEnv);
        if (!analyzeResult.isSuccess()) {
            throw new ServiceException("组件代码生成失败");
        }
        long time3 = System.currentTimeMillis();
        // key 是以.java 结尾的全路径名称
        Map<String, String> scriptMap = analyzeResult.getScriptMap();
        log.info("单个变量测试生成的代码:{}", scriptMap);
        //根据直接引用callInfo信息查询到间接引用数据
        VarFunctionCompileQueryResultDto queryResult = findCallInfoSet(space.getId(),interfaceId, analyzeResult.getSyntaxInfo().getCallInfo());

        Map<String, String> javaCodeMap = new HashMap<>(MagicNumbers.INT_64);
        String packageName = MessageFormat.format("{0}.{1}", PackageGenerator.ENGINE_PACKAGE,
            MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, String.valueOf(interfaceId)));
        String varClsName = MessageFormat.format("{0}.{1}.java", packageName,
            MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, String.valueOf(interfaceId)));

        if (scriptMap.containsKey(varClsName)) {
            javaCodeMap.put(varClsName, scriptMap.get(varClsName));
        }
        //按需生成并编译rawData并标记运行时装载
        log.info("变量服务打包，模型代码生成开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode,
            serviceCode, interfaceId);

        javaCodeMap.putAll(generateCompileInputMark(interfaceId, space.getInputData(), spaceCode,
            serviceCode, mainEngine.getService().getServer().getDecision(), queryResult, attrSplitThreshold));
        javaCodeMap.putAll(generateCompileVarsDataMark(interfaceId, spaceCode, serviceCode,
            mainEngine, queryResult, varDefSet));

        //补充一下运行时需要涉及的其它公共函数,因为有可能是未审核的测试，所以后端可能无法提前预判，需要根据引擎分析结果实时查询
        Map<String, byte[]> dependsCls = new HashMap<>(MagicNumbers.INT_64);
        List<SyntaxInfo.CallInfo> callInfoList = analyzeResult.getSyntaxInfo().getCallInfo().stream()
                .filter(e -> EngineComponentInvokeMetaTypeEnum.COMPONENT == e.getType())
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(callInfoList)) {
            log.info("变量测试，补充其它变量class开始:varId:{},varName:{} identifier:{},changeNum:{} ",
                    flowData.getVarId(), flowData.getName(), flowData.getIdentifier(), flowData.getChangeNum());
            List<String> javaClsList = queryResult.getJavaClsList();
            for (String javaCls : javaClsList) {
                Map<String,String> compiledClsMap = JSON.parseObject(javaCls, Map.class);
                compiledClsMap.entrySet().forEach(entry -> {
                    dependsCls.put(entry.getKey(), Base64.getDecoder().decode(entry.getValue()));
                });
            }
        }

        long time4 = System.currentTimeMillis();
        log.info("变量服务打包，代码编译开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode,
            serviceCode, interfaceId);

        // key 是不以.java 结尾的全路径名称
        Map<String, byte[]> varBytes = compile(mainEngine.getService().getServer().getDecision(), space.getCode(), processService.getCode(),
            interfaceId, javaCodeMap, dependsCls);
        varBytes.putAll(dependsCls);
        log.info("变量服务打包，空间：{}的服务：{}下接口：{}打包完成,耗时： 数据查询 {}ms  变量代码生成:{} 其它代码生成:{} 编译:{} ",
            spaceCode, serviceCode, interfaceId, time2 - time1, time3 - time2, time4 - time3,
            System.currentTimeMillis() - time4);

        return JSON.toJSONString(varBytes).getBytes(StandardCharsets.UTF_8);
    }

    private void fillOtherCls(Set<VarCompileData> otherDatas,Set<String> nodeIdentifierSet,Map<String, byte[]> varBytes) {
		otherDatas.forEach(data -> {
			if (nodeIdentifierSet.contains(data.getIdentifier())) {
				Map<String,String> compiledClsMap = JSON.parseObject(data.getJavaCls(), Map.class); 
				compiledClsMap.entrySet().forEach(entry -> {
                    varBytes.put(entry.getKey(), Base64.getDecoder().decode(entry.getValue()));
                });
			}
			});
	}

    private VarContainer generateCompileBuildRuntimeContainer(VarProcessSpace space, VarProcessService processService, VarProcessInterface processInterface,
                                                              CompileEnvEnum compileEnv, Boolean loadCls, Set<VarDto> varDefSet) throws Throwable {
        Long interfaceId = processInterface.getId();
        String spaceCode = space.getCode();
        String serviceCode = processService.getCode();
        Engine mainEngine = generateCompileBuildRuntimeContainer(space, processService, processInterface);
        VarContainer decision = mainEngine.getService().getServer().getDecision();
        log.info("变量清单测试功能，数据准备开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode, serviceCode, interfaceId);
        long time1 = System.currentTimeMillis();
        VarCompileData flowData = varProvider.getInterfaceFlowData(interfaceId);
        Map<String, JSONObject> templateConfig = varProvider.getTemplateConfig(space.getId(), null);
        long time2 = System.currentTimeMillis();
        //补充数据
        flowData.setInterfaceId(interfaceId);
        Assert.notNull(flowData, "未查询到服务的变量信息");
        log.info("变量清单测试功能，变量代码生成开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode,
            serviceCode, interfaceId);
        VarCompileResult analyzeResult = varCompiler.compile(flowData, templateConfig, true,
            mainEngine, compileEnv);
        if (!analyzeResult.isSuccess()) {
            throw new ServiceException("组件代码生成失败");
        }
        long time3 = System.currentTimeMillis();
        // key 是以.java 结尾的全路径名称
        Map<String, String> scriptMap = analyzeResult.getScriptMap();
        log.info("变量清单测试生成的代码:{}", scriptMap);
        //根据直接引用callInfo信息查询到间接引用数据
        VarFunctionCompileQueryResultDto queryResult = findCallInfoSet(space.getId(), interfaceId, analyzeResult.getSyntaxInfo().getCallInfo());

        Map<String, String> javaCodeMap = new HashMap<>(MagicNumbers.INT_64);
        String packageName = MessageFormat.format("{0}.{1}", PackageGenerator.ENGINE_PACKAGE,
            MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, String.valueOf(interfaceId)));
        String varClsName = MessageFormat.format("{0}.{1}.java", packageName,
            MessageFormat.format(ComponentClassNameGenerator.CLS_NAME_FORMAT, String.valueOf(interfaceId)));

        if (scriptMap.containsKey(varClsName)) {
            javaCodeMap.put(varClsName, scriptMap.get(varClsName));
        }
        //按需生成并编译rawData并标记运行时装载
        log.info("变量清单测试功能，模型变量代码生成开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode,
            serviceCode, interfaceId);
        javaCodeMap.putAll(generateCompileInputMark(interfaceId, space.getInputData(), spaceCode,
            serviceCode, mainEngine.getService().getServer().getDecision(), queryResult, attrSplitThreshold));
        javaCodeMap.putAll(generateCompileVarsDataMark(interfaceId, spaceCode, serviceCode,
            mainEngine, queryResult, varDefSet));

        //补充一下运行时需要涉及的其它公共函数,因为有可能是未审核的测试，所以后端可能无法提前预判，需要根据引擎分析结果实时查询
        Map<String, byte[]> dependsCls = new HashMap<>(MagicNumbers.INT_64);
        List<SyntaxInfo.CallInfo> callInfoList = analyzeResult.getSyntaxInfo().getCallInfo().stream()
                .filter(e -> EngineComponentInvokeMetaTypeEnum.COMPONENT == e.getType())
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(callInfoList)) {
            log.info("变量测试，补充其它变量class开始:varId:{},varName:{} identifier:{},changeNum:{} ",
                    flowData.getVarId(), flowData.getName(), flowData.getIdentifier(), flowData.getChangeNum());
            List<String> javaClsList = queryResult.getJavaClsList();
            for (String javaCls : javaClsList) {
                Map<String,String> compiledClsMap = JSON.parseObject(javaCls, Map.class);
                compiledClsMap.entrySet().forEach(entry -> {
                    dependsCls.put(entry.getKey(), Base64.getDecoder().decode(entry.getValue()));
                });
            }
        }
        long time4 = System.currentTimeMillis();
        // key 是不以.java 结尾的全路径名称
        log.info("变量清单测试功能，变量编译开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode,
            serviceCode, interfaceId);
        Map<String, byte[]> varBytes = compile(decision, space.getCode(), processService.getCode(),
            interfaceId, javaCodeMap, dependsCls);
        varBytes.putAll(dependsCls);

        long time5 = System.currentTimeMillis();
        log.info("变量清单测试功能，变量load开始，spaceCode:{},serviceCode:{} interfaceId:{}", spaceCode,
            serviceCode, interfaceId);
        loadEngineComponents(mainEngine, flowData, mainEngine.loadClassMap(varBytes), queryResult);
        log.info("变量清单测试功能，空间：{}的服务：{}下接口：{}容器准备完成,耗时： 数据查询 {}ms  变量代码生成:{} 其它代码生成:{} 编译:{} load:{}",
            spaceCode, serviceCode, interfaceId, time2 - time1, time3 - time2, time4 - time3,
            time5 - time4, System.currentTimeMillis() - time5);
        return decision;
    }

    /**
     * 初始构造容器
     * @param space 变量空间信息
     * @param processService 变量服务信息
     * @param processInterface 服务接口信息
     * @return 构造容器
     * @throws Throwable 抛出异常
     */
    private Engine generateCompileBuildRuntimeContainer(VarProcessSpace space,
                                                        VarProcessService processService,
                                                        VarProcessInterface processInterface) {
        //构建容器并初始化引擎
        String classPath = new File(clsSavePath).toPath().toString();
        VarContainer decision = new VarContainer(classPath, libJarPath, false);
        return buildEngine(processInterface, space, processService, decision);
    }

    /**
     * 代码编译
     * @param decision decision
     * @param spaceCode 空间编码
     * @param serviceCode 服务编码
     * @param interfaceId 接口id
     * @param codeMap 编译code信息
     * @param dependsCls 依赖信息
     * @return 编译后的信息
     */
    private Map<String, byte[]> compile(VarContainer decision, String spaceCode, String serviceCode, Long interfaceId,
                                        Map<String, String> codeMap, Map<String, byte[]> dependsCls) {
        log.debug("代码编译,spaceCode:{} serviceCode:{} interfaceId:{},codeMap:{}", spaceCode,
            serviceCode, interfaceId, codeMap);
        try {
            return decision.compileEngineClassByJavaCode(spaceCode, serviceCode,
                interfaceId.toString(), codeMap, dependsCls);
        } catch (ServiceException e) {
            log.warn("compile var failed", e);
            throw e;
        } catch (Throwable e) {
            log.warn("compile var failed", e);
            throw new ServiceException("编译失败:" + e.getMessage());
        }
    }

    private static void loadEngineComponents(Engine mainEngine, VarCompileData flowData, Map<String,Class<?>> classMap,VarFunctionCompileQueryResultDto queryResult) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
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

    private static Engine buildEngine(VarProcessInterface processInterface, VarProcessSpace space,
                                      VarProcessService processService, VarContainer decision) {
        Server server = decision.addServer(space.getCode());
        server.setSpace(space);
        Service serviceContainer = server.addService(processService.getCode());
        Engine mainEngine = serviceContainer.addEngine(processInterface.getId().toString());
        mainEngine.setVarInterface(processInterface);
        return mainEngine;
    }

    private static Map<String,String> generateCompileInputMark(Long interfaceId, String inputJsonSchema,String spaceCode, String serviceCode, VarContainer decision, VarFunctionCompileQueryResultDto queryResult, int attrSplitThreshold) throws Throwable {
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
			log.info("rawData代码生成，interfaceId:{},varSet:{}",interfaceId,varSet);
			Map<String,String> codeMap =  decision.generateEngineClassByPropertyPathsAndPropertyPrefix(spaceCode, serviceCode, interfaceId.toString(), varSet, inputMap,"rawData", attrSplitThreshold);
			log.info("rawData代码生成，interfaceId:{},code:{}",interfaceId,codeMap);
			return codeMap;
			
		}
		
		return new HashMap<>(MagicNumbers.INT_1);
	}

    private static Map<String,String> generateCompileVarsDataMark(Long interfaceId, String spaceCode, String serviceCode,Engine mainEngine,VarFunctionCompileQueryResultDto queryResult,Set<VarDto> varDefSet) throws Throwable {
		Map<String,VarDto> varDefMap = new HashMap<>(MagicNumbers.EIGHT);
		varDefSet.stream().forEach(varDef -> varDefMap.put(varDef.getName(), varDef));
		
		Set<String> varSet = new HashSet<>();
		List<Field> varsFieldLst = new ArrayList<>();
        if (queryResult.getVarPathSet() != null) {
            queryResult.getVarPathSet().forEach((varName) -> {
                if (varName.toLowerCase().startsWith(DataValuePrefixEnum.VARS.name().toLowerCase()) && !varSet.contains(varName)) {
                    varSet.add(varName);
                    String realVarName = (varName.split("\\."))[1];
                    //肯定是简单类型,定义信息由外部传入
                    Assert.notNull(varDefMap.get(realVarName),MessageFormat.format("变量[{0}]没有添加到变量清单中",realVarName));
                    varsFieldLst.add(new Field(realVarName,varDefMap.get(realVarName).getType(),false));
                }
            });
        }

		mainEngine.getService().getServer().setVars(true);
		Map parameterMap = new HashMap(MagicNumbers.EIGHT);
		parameterMap.put(MessageFormat.format("{0}", PackageGenerator.ENGINE_PACKAGE), Collections.singletonList(new Pair<>("vars", VarContainer.generateJsonSchemaByFields("vars", varsFieldLst))));
		log.info("vars代码生成,interfaceId：{} spaceCode:{} serviceCode:{} parameterMap:{}",interfaceId,spaceCode,serviceCode,parameterMap);

		Map<String,String> varCode =  mainEngine.getService().getServer().getDecision().generateEngineClassByJsonSchema(spaceCode, serviceCode, interfaceId.toString(), parameterMap);
		log.info("vars代码生成,interfaceId：{} spaceCode:{} serviceCode:{} varCode:{}",interfaceId,spaceCode,serviceCode,varCode);
		return varCode;
	}
    /**
     * 查询某个组件的穿透引用信息
     * @param spaceId 空间id
     * @param manifestId 清单id
     * @param entryComponentCallInfo 直接引用信息
     * @return 直接引用包括间接引用组件变量信息
     */
    private VarFunctionCompileQueryResultDto findCallInfoSet(Long spaceId, Long manifestId, List<SyntaxInfo.CallInfo> entryComponentCallInfo) {
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
                    VarFunctionCompileQueryResultDto resultDto = varProcessCompileVarProvider.getAllIdentifiersAndVarPathes(spaceId, manifestId, varType, callInfo.getValue(),false);
                    varComponentSet.addAll(resultDto.getVarComponentSet());
                    functionComponentSet.addAll(resultDto.getFunctionComponentSet());
                    varPathSet.addAll(resultDto.getVarPathSet());
                    modelTypeSet.addAll(resultDto.getModelTypeSet());
                    javaClsList.addAll(resultDto.getJavaClsList());
                } else if (varType == VarTypeEnum.FUNCTION && !functionComponentSet.contains(callInfo.getValue())) {
                    functionComponentSet.add(callInfo.getValue());
                    VarFunctionCompileQueryResultDto resultDto = varProcessCompileVarProvider.getAllIdentifiersAndVarPathes(spaceId, manifestId, varType, callInfo.getValue(),false);
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

}
