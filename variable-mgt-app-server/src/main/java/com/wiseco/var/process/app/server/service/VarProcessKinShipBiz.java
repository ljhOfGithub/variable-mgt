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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.vo.output.KinShipOutputVo;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.KinShipTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingMapper;
import com.wiseco.var.process.app.server.repository.VarProcessServiceVersionMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingService;
import com.wiseco.var.process.app.server.service.dto.ServiceUsingManifestDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestFunctionService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVarService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VarProcessKinShipBiz {


    public static final String RAW_DATA = "rawData.";
    @Resource
    private VarProcessManifestService       varProcessManifestService;

    @Resource
    private BacktrackingService             backtrackingService;

    @Resource
    private VarProcessVariableVarService varProcessVariableVarService;

    @Resource
    private VarProcessFunctionVarService varProcessFunctionVarService;

    @Resource
    private VarProcessManifestVarService varProcessManifestVarService;

    @Resource
    private VarProcessRealtimeServiceService varProcessRealtimeServiceService;

    @Resource
    private VarProcessServiceVersionMapper varProcessServiceVersionMapper;

    @Resource
    private VarProcessBatchBacktrackingMapper varProcessBatchBacktrackingMapper;

    @Resource
    private VarProcessServiceManifestService varProcessServiceManifestService;

    @Resource
    private VarProcessManifestVariableService varProcessManifestVariableService;

    @Resource
    private VarProcessVariableFunctionService   varProcessVariableFunctionService;

    @Resource
    private VarProcessFunctionReferenceService  varProcessFunctionReferenceService;

    @Resource
    private VarProcessVariableReferenceService varProcessVariableReferenceService;

    @Resource
    private VarProcessVariableService varProcessVariableService;

    @Resource
    private VarProcessManifestFunctionService varProcessManifestFunctionService;

    @Resource
    private VarProcessFunctionService varProcessFunctionService;


    /**
     * 血缘关系
     *
     * @param spaceId  空间ID
     * @param varFullPath 对象名称
     * @param id id(所有的id)
     * @param type  类型
     * @return list
     */
    public List<KinShipOutputVo> getKinShipList(Long spaceId, String varFullPath, Long id, KinShipTypeEnum type) {

        List<KinShipOutputVo> result = new ArrayList<>();
        switch (type) {
            case DATA_MODEL:
                result = getDataModelVarUseList(spaceId,varFullPath);
                break;
            case VARIABLE:
                result = getVariableUseList(id);
                break;
            case TEMPLATE:
            case PREP:
            case FUNCTION:
                result = findFuncUseList(id,spaceId,type);
                break;
            case MANIFEST:
                result = findManifestUseList(id,spaceId);
                break;
            default:
                break;
        }

        Set<Long> usedVariableIds = findUsedVariables(spaceId);
        Set<Long> usedManifestIds = findUsedManifests(spaceId);
        Map<KinShipTypeEnum,Set<Long>> usedFunctionMap = findUsedFunctions(spaceId);

        //设置是否有下一层
        for (KinShipOutputVo kinShipOutputVo :  result) {
            for (KinShipOutputVo.NodeInfo nodeInfo : kinShipOutputVo.getChildren()) {
                if (kinShipOutputVo.getType() == KinShipTypeEnum.VARIABLE) {
                    nodeInfo.setHasChildren(usedVariableIds.contains(nodeInfo.getId()));
                } else if (kinShipOutputVo.getType() == KinShipTypeEnum.TEMPLATE || kinShipOutputVo.getType() == KinShipTypeEnum.PREP || kinShipOutputVo.getType() == KinShipTypeEnum.FUNCTION) {
                    Set<Long> usedFunctionIds = usedFunctionMap.get(kinShipOutputVo.getType());
                    nodeInfo.setHasChildren(usedFunctionIds.contains(nodeInfo.getId()));
                } else  if (kinShipOutputVo.getType() == KinShipTypeEnum.MANIFEST) {
                    nodeInfo.setHasChildren(usedManifestIds.contains(nodeInfo.getId()));
                } else if (kinShipOutputVo.getType() == KinShipTypeEnum.SERVICE || kinShipOutputVo.getType() == KinShipTypeEnum.BACKTRACKING) {
                    nodeInfo.setHasChildren(false);
                } else {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, kinShipOutputVo.getType() + "未知枚举类型");
                }
            }
        }

        return result;


    }

    /**
     * 获取被使用的公共函数
     * @param spaceId 空间id
     * @return map
     */
    private Map<KinShipTypeEnum, Set<Long>> findUsedFunctions(Long spaceId) {
        Map<KinShipTypeEnum, Set<Long>> usedFunctionMap = new HashMap<>(MagicNumbers.THREE);

        Set<Long> functionsUsedByFunction = varProcessFunctionReferenceService.findUsedFunctions(spaceId);
        Set<String> functionsUsedByManifest = varProcessManifestFunctionService.findUsedFunctions(spaceId);
        Set<Long> functionsUsedByVariable = varProcessVariableFunctionService.findUsedFunctions(spaceId);

        Map<FunctionTypeEnum, List<VarProcessFunction>> functionsUsedByManifestMap = new HashMap<>(MagicNumbers.TWO);
        if (!CollectionUtils.isEmpty(functionsUsedByManifest)) {
            functionsUsedByManifestMap = varProcessFunctionService.list(Wrappers.<VarProcessFunction>lambdaQuery().select(VarProcessFunction::getFunctionType, VarProcessFunction::getId)
                            .in(VarProcessFunction::getIdentifier, functionsUsedByManifest).eq(VarProcessFunction::getDeleteFlag,DeleteFlagEnum.USABLE.getCode()))
                    .stream().collect(Collectors.groupingBy(VarProcessFunction::getFunctionType));
        }

        //公共方法：目前会被变量模板/预处理/清单使用
        Set<Long> usedFunctions = new HashSet<>(functionsUsedByFunction);
        List<VarProcessFunction> funcsUsedByManifest = functionsUsedByManifestMap.get(FunctionTypeEnum.FUNCTION);
        if (!CollectionUtils.isEmpty(funcsUsedByManifest)) {
            usedFunctions.addAll(funcsUsedByManifest.stream().map(VarProcessFunction::getId).collect(Collectors.toSet()));
        }
        usedFunctionMap.put(KinShipTypeEnum.FUNCTION,usedFunctions);

        //预处理：目前只会被清单使用
        List<VarProcessFunction> prepsUsedByManifest = functionsUsedByManifestMap.get(FunctionTypeEnum.PREP);
        if (!CollectionUtils.isEmpty(prepsUsedByManifest)) {
            usedFunctionMap.put(KinShipTypeEnum.PREP,prepsUsedByManifest.stream().map(VarProcessFunction::getId).collect(Collectors.toSet()));
        } else {
            usedFunctionMap.put(KinShipTypeEnum.PREP,new HashSet<>());
        }

        //变量模板：目前只会被变量使用
        usedFunctionMap.put(KinShipTypeEnum.TEMPLATE,functionsUsedByVariable);

        return usedFunctionMap;
    }

    /**
     * 获取被使用的清单id集合
     * @param spaceId 空间id
     * @return set
     */
    private Set<Long> findUsedManifests(Long spaceId) {
        Set<Long> usedManifestIds = new HashSet<>();
        //实时服务
        usedManifestIds.addAll(varProcessServiceManifestService.findUsedManifests(spaceId));
        //被批量回溯指标使用
        usedManifestIds.addAll(backtrackingService.findUsedManifests(spaceId));
        return usedManifestIds;
    }

    /**
     * 获取被使用的变量id集合
     * @param spaceId 空间id
     * @return set
     */
    private Set<Long> findUsedVariables(Long spaceId) {
        Set<Long> usedVariableIds = new HashSet<>();
        //被清单使用
        usedVariableIds.addAll(varProcessManifestVariableService.findUsedVariables(spaceId));
        //被其他指标使用
        usedVariableIds.addAll(varProcessVariableReferenceService.findUsedVariables(spaceId));
        return usedVariableIds;
    }

    /**
     *
     * @param spaceId 空间ID
     * @param varFullPath 路径
     * @return 数据模型变量使用情况
     */
    public List<KinShipOutputVo> getDataModelVarUseList(Long spaceId, String varFullPath) {
        varFullPath = PositionVarEnum.RAW_DATA.getName() + "." + varFullPath;
        String finalVarFullPath = varFullPath;
        CompletableFuture<KinShipOutputVo> future1 = CompletableFuture.supplyAsync(() -> getDataModelVar(finalVarFullPath, spaceId));
        CompletableFuture<KinShipOutputVo> future2 = CompletableFuture.supplyAsync(() -> getDataModelPre(finalVarFullPath, spaceId));
        CompletableFuture<KinShipOutputVo> future3 = CompletableFuture.supplyAsync(() -> getDataModelFunction(finalVarFullPath, spaceId));
        CompletableFuture<KinShipOutputVo> future4 = CompletableFuture.supplyAsync(() -> getDataModelTemplate(finalVarFullPath, spaceId));
        CompletableFuture<KinShipOutputVo> future5 = CompletableFuture.supplyAsync(() -> getDataModelManifest(finalVarFullPath, spaceId));
        CompletableFuture<KinShipOutputVo> future6 = CompletableFuture.supplyAsync(() -> getDataModelService(finalVarFullPath));
        CompletableFuture<KinShipOutputVo> future7 = CompletableFuture.supplyAsync(() -> getDataModelBacktracking(finalVarFullPath));

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3, future4, future5, future6, future7);

        allFutures.join();

        // 使用join()方法获取每个任务的结果，并将结果合成一个List
        List<KinShipOutputVo> resultList = allFutures.thenApply(v ->
                        Arrays.asList(future1, future2, future3, future4, future5, future6, future7)
                                .stream()
                                .map(CompletableFuture::join)
                                .filter(result -> result != null)
                                .collect(Collectors.toList()))
                .join();

        return resultList;
    }


    /**
     * 变量使用
     *
     * @param id 变量id
     * @return 变量使用list
     */
    public List<KinShipOutputVo> getVariableUseList(Long id) {

        //被其他变量使用
        List<Long> variableIdList = varProcessVariableReferenceService.list(
                        new LambdaQueryWrapper<VarProcessVariableReference>().select(VarProcessVariableReference::getUseByVariableId).eq(VarProcessVariableReference::getVariableId, id))
                .stream().map(VarProcessVariableReference::getUseByVariableId).collect(Collectors.toList());
        List<KinShipOutputVo> outputList = new ArrayList<>();
        if (!org.springframework.util.CollectionUtils.isEmpty(variableIdList)) {
            List<VarProcessVariable> varProcessVariableList = varProcessVariableService.list(
                    new LambdaQueryWrapper<VarProcessVariable>()
                            .select(VarProcessVariable::getId, VarProcessVariable::getLabel, VarProcessVariable::getName, VarProcessVariable::getVersion)
                            .in(VarProcessVariable::getId, variableIdList).eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            List<KinShipOutputVo.NodeInfo> children = varProcessVariableList.stream()
                    .map(variable -> KinShipOutputVo.NodeInfo.builder().id(variable.getId()).name(variable.getLabel()).code(variable.getName()).version(variable.getVersion()).build())
                    .collect(Collectors.toList());
            outputList.add(KinShipOutputVo.builder().type(KinShipTypeEnum.VARIABLE).children(children).build());
        }

        //被变量清单使用
        List<Long> manifestIdList = varProcessManifestVariableService.list(
                        new LambdaQueryWrapper<VarProcessManifestVariable>().select(VarProcessManifestVariable::getManifestId).eq(VarProcessManifestVariable::getVariableId, id))
                .stream().map(VarProcessManifestVariable::getManifestId).collect(Collectors.toList());

        if (!org.springframework.util.CollectionUtils.isEmpty(manifestIdList)) {
            List<VarProcessManifest> manifestList = varProcessManifestService.list(
                    new LambdaQueryWrapper<VarProcessManifest>()
                            .select(VarProcessManifest::getId, VarProcessManifest::getVarManifestName)
                            .in(VarProcessManifest::getId, manifestIdList).eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            List<KinShipOutputVo.NodeInfo> children = manifestList.stream()
                    .map(variable -> KinShipOutputVo.NodeInfo.builder().id(variable.getId()).name(variable.getVarManifestName()).build())
                    .collect(Collectors.toList());
            outputList.add(KinShipOutputVo.builder().type(KinShipTypeEnum.MANIFEST).children(children).build());

        }
        log.info("变量清单-血缘关系,outputList:{}", outputList);
        return outputList;
    }

    /**
     * getPrepDataModelVarUseList
     *
     * @param varUseList 变量使用列表
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelVarUseOutputVo
     */
    public KinShipOutputVo getPrepDataModelVarUseList(List<VariableUseVarPathDto> varUseList) {
        Map<Long, VariableUseVarPathDto> varUseMap = varUseList.stream().collect(Collectors.toMap(VariableUseVarPathDto::getId, value -> value, (key1, key2) -> key1));

        KinShipOutputVo kinShipOutputVo = new KinShipOutputVo();
        kinShipOutputVo.setName(KinShipTypeEnum.PREP.getDesc());
        kinShipOutputVo.setType(KinShipTypeEnum.PREP);
        List<KinShipOutputVo.NodeInfo> nodeInfoList = new ArrayList<>();
        Set<Map.Entry<Long, VariableUseVarPathDto>> entries = varUseMap.entrySet();
        for (Map.Entry<Long, VariableUseVarPathDto> entry : entries) {
            VariableUseVarPathDto pre = entry.getValue();
            nodeInfoList.add(KinShipOutputVo.NodeInfo.builder().id(pre.getId()).name(pre.getName()).version(pre.getVersion()).build());

        }
        kinShipOutputVo.setChildren(nodeInfoList);

        return kinShipOutputVo;
    }

    /**
     * getFunctionDataModelVarUseList
     *
     * @param varUseList 变量使用列表
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelVarUseOutputVo
     */
    public KinShipOutputVo getFunctionDataModelVarUseList(List<VariableUseVarPathDto> varUseList) {
        Map<Long, VariableUseVarPathDto> varUseMap = varUseList.stream().collect(Collectors.toMap(VariableUseVarPathDto::getId, value -> value, (key1, key2) -> key1));

        KinShipOutputVo kinShipOutputVo = new KinShipOutputVo();
        kinShipOutputVo.setName(KinShipTypeEnum.FUNCTION.getDesc());
        kinShipOutputVo.setType(KinShipTypeEnum.FUNCTION);
        List<KinShipOutputVo.NodeInfo> nodeInfoList = new ArrayList<>();
        Set<Map.Entry<Long, VariableUseVarPathDto>> entries = varUseMap.entrySet();
        for (Map.Entry<Long, VariableUseVarPathDto> entry : entries) {
            VariableUseVarPathDto pre = entry.getValue();
            nodeInfoList.add(KinShipOutputVo.NodeInfo.builder().id(pre.getId()).name(pre.getName()).version(pre.getVersion()).build());

        }
        kinShipOutputVo.setChildren(nodeInfoList);

        return kinShipOutputVo;
    }

    /**
     * getTemplateDataModelVarUseList
     *
     * @param varUseList 变量使用列表
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelVarUseOutputVo
     */
    public KinShipOutputVo getTemplateDataModelVarUseList(List<VariableUseVarPathDto> varUseList) {
        Map<Long, VariableUseVarPathDto> varUseMap = varUseList.stream().collect(Collectors.toMap(VariableUseVarPathDto::getId, value -> value, (key1, key2) -> key1));

        KinShipOutputVo kinShipOutputVo = new KinShipOutputVo();
        kinShipOutputVo.setName(KinShipTypeEnum.TEMPLATE.getDesc());
        kinShipOutputVo.setType(KinShipTypeEnum.TEMPLATE);
        List<KinShipOutputVo.NodeInfo> nodeInfoList = new ArrayList<>();
        Set<Map.Entry<Long, VariableUseVarPathDto>> entries = varUseMap.entrySet();
        for (Map.Entry<Long, VariableUseVarPathDto> entry : entries) {
            VariableUseVarPathDto pre = entry.getValue();
            nodeInfoList.add(KinShipOutputVo.NodeInfo.builder().id(pre.getId()).name(pre.getName()).version(pre.getVersion()).build());

        }
        kinShipOutputVo.setChildren(nodeInfoList);

        return kinShipOutputVo;
    }


    /**
     * 获取公共函数引用关系
     *
     * @param functionId 公共函数id
     * @param spaceId 空间id
     * @param type 类型枚举
     * @return 血缘关系出参list
     */
    public List<KinShipOutputVo> findFuncUseList(Long functionId, Long spaceId, KinShipTypeEnum type) {
        List<KinShipOutputVo> outputVos;
        if (KinShipTypeEnum.PREP.equals(type)) {
            outputVos = findPrepUseList(functionId,spaceId);
        } else if (KinShipTypeEnum.TEMPLATE.equals(type)) {
            outputVos = findTemplateUseList(functionId,spaceId);
        } else {
            outputVos = findFunctionUseList(functionId,spaceId);
        }
        return outputVos;
    }

    private List<KinShipOutputVo> findPrepUseList(Long prepId, Long spaceId) {
        List<KinShipOutputVo> outputVos = new ArrayList<>();
        List<VarProcessManifest> referenceList = varProcessManifestService.findManifestUsingFunc(prepId,spaceId);
        if (!CollectionUtils.isEmpty(referenceList)) {
            List<KinShipOutputVo.NodeInfo> children = referenceList.stream()
                    .map(manifest -> KinShipOutputVo.NodeInfo.builder().id(manifest.getId()).name(manifest.getVarManifestName()).build())
                    .collect(Collectors.toList());
            outputVos.add(KinShipOutputVo.builder().type(KinShipTypeEnum.MANIFEST).children(children).build());
        }
        return outputVos;
    }

    private List<KinShipOutputVo> findTemplateUseList(Long tempId, Long spaceId) {
        List<KinShipOutputVo> outputVos = new ArrayList<>();
        List<VarProcessVariable> referenceList = varProcessVariableFunctionService.findVariableUseTemp(tempId,spaceId);
        if (!CollectionUtils.isEmpty(referenceList)) {
            List<KinShipOutputVo.NodeInfo> children = referenceList.stream()
                    .map(variable -> KinShipOutputVo.NodeInfo.builder().id(variable.getId()).name(variable.getLabel()).code(variable.getName()).version(variable.getVersion()).build())
                    .collect(Collectors.toList());
            outputVos.add(KinShipOutputVo.builder().type(KinShipTypeEnum.VARIABLE).children(children).build());
        }
        return outputVos;
    }

    private List<KinShipOutputVo> findFunctionUseList(Long funcid, Long spaceId) {
        List<KinShipOutputVo> outputVos = new ArrayList<>();

        //被其他公共方法/变量模板/预处理使用
        Map<FunctionTypeEnum, List<VarProcessFunction>> functionRefMap = varProcessFunctionReferenceService.findFunctionRef(funcid, spaceId);
        if (!CollectionUtils.isEmpty(functionRefMap)) {
            Map <FunctionTypeEnum,KinShipTypeEnum> functypeMap = new HashMap<>(MagicNumbers.EIGHT);
            functypeMap.put(FunctionTypeEnum.TEMPLATE,KinShipTypeEnum.TEMPLATE);
            functypeMap.put(FunctionTypeEnum.PREP,KinShipTypeEnum.PREP);
            functypeMap.put(FunctionTypeEnum.FUNCTION,KinShipTypeEnum.FUNCTION);
            for (Map.Entry<FunctionTypeEnum, List<VarProcessFunction>> entry : functionRefMap.entrySet()) {
                List<KinShipOutputVo.NodeInfo> children = entry.getValue().stream()
                        .map(func -> KinShipOutputVo.NodeInfo.builder().id(func.getId()).name(func.getName()).build())
                        .collect(Collectors.toList());
                outputVos.add(KinShipOutputVo.builder().type(functypeMap.get(entry.getKey())).children(children).build());
            }
        }

        //被变量清单使用
        outputVos.addAll(findPrepUseList(funcid,spaceId));
        return outputVos;
    }
    /**
     * 获取变量清单的引用关系
     *
     * @param manifestId 清单id
     * @param spaceId 空间id
     * @return 血缘关系出参list
     */
    public List<KinShipOutputVo> findManifestUseList(Long manifestId, Long spaceId) {
        List<KinShipOutputVo> outputVos = new ArrayList<>();
        //被实时服务使用
        List<ServiceUsingManifestDto> usingServices = varProcessManifestService.findUsingService(manifestId,spaceId);
        if (!CollectionUtils.isEmpty(usingServices)) {
            List<KinShipOutputVo.NodeInfo> children = usingServices.stream()
                    .map(service -> KinShipOutputVo.NodeInfo.builder().id(service.getServiceId()).name(service.getName()).version(service.getVersion()).build())
                    .collect(Collectors.toList());
            outputVos.add(KinShipOutputVo.builder().type(KinShipTypeEnum.SERVICE).children(children).build());
        }
        //被批量回溯任务使用
        List<VarProcessBatchBacktracking> backTrackingTasks = backtrackingService.list(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getId, VarProcessBatchBacktracking::getName)
                .eq(VarProcessBatchBacktracking::getManifestId, manifestId)
                .eq(VarProcessBatchBacktracking::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (!CollectionUtils.isEmpty(backTrackingTasks)) {
            List<KinShipOutputVo.NodeInfo> children = backTrackingTasks.stream()
                    .map(task -> KinShipOutputVo.NodeInfo.builder().id(task.getId()).name(task.getName()).build())
                    .collect(Collectors.toList());
            outputVos.add(KinShipOutputVo.builder().type(KinShipTypeEnum.BACKTRACKING).children(children).build());
        }
        return outputVos;
    }
    /**
     * 数据模型变量被变量使用
     * @param varFullPath 路径
     * @param spaceId 空间ID
     * @return KinShipOutputVo
     */
    public KinShipOutputVo getDataModelVar(String varFullPath, Long spaceId) {
        KinShipOutputVo outputVo = null;
        List<VariableUseVarPathDto> queryList = varProcessVariableVarService.getVarUseList(spaceId);
        if (!org.springframework.util.CollectionUtils.isEmpty(queryList)) {
            List<VariableUseVarPathDto> varUseList = varProcessVariableVarService.getVarUse(queryList, varFullPath);
            if (!org.springframework.util.CollectionUtils.isEmpty(varUseList)) {
//                addVarUsed(outputList, varUseList);
                Set<VariableUseVarPathDto> varUseSet = new HashSet<>(varUseList);
                Map<String, List<VariableUseVarPathDto>> varUseMap = varUseSet.stream().collect(Collectors.groupingBy(VariableUseVarPathDto::getName));

                List<VariableUseVarPathDto> combinedList = new ArrayList<>();
                Set<Map.Entry<String, List<VariableUseVarPathDto>>> entries = varUseMap.entrySet();
                for (Map.Entry<String, List<VariableUseVarPathDto>> entry : entries) {
                    List<VariableUseVarPathDto> variableList = entry.getValue();
                    Comparator<VariableUseVarPathDto> comparator = Comparator.comparingInt(VariableUseVarPathDto::getVersion).reversed();
                    variableList.sort(comparator);
                    combinedList.addAll(variableList);
                }

                if (!CollectionUtils.isEmpty(combinedList)) {
                    List<KinShipOutputVo.NodeInfo> children = combinedList.stream()
                            .map(variable -> KinShipOutputVo.NodeInfo.builder().id(variable.getId()).name(variable.getLabel()).code(variable.getName()).version(variable.getVersion()).build())
                            .collect(Collectors.toList());
                    outputVo = KinShipOutputVo.builder().type(KinShipTypeEnum.VARIABLE).children(children).build();
                }
            }
        }
        return outputVo;
    }
    /**
     * 数据模型变量被预处理使用
     * @param varFullPath 路径
     * @param spaceId 空间ID
     * @return KinShipOutputVo
     */
    public KinShipOutputVo getDataModelPre(String varFullPath,Long spaceId) {
        KinShipOutputVo outputVo = null;
        List<VariableUseVarPathDto> prepQueryList = varProcessFunctionVarService.getFunctionPrepDataModelVarUseList(spaceId);
        if (!org.springframework.util.CollectionUtils.isEmpty(prepQueryList)) {
            List<VariableUseVarPathDto> varUseList = varProcessVariableVarService.getVarUse(prepQueryList, varFullPath);
            Map<FunctionTypeEnum, List<VariableUseVarPathDto>> functionUseMap = varUseList.stream().collect(Collectors.groupingBy(VariableUseVarPathDto::getFunctionType));
            if (!org.springframework.util.CollectionUtils.isEmpty(varUseList)) {
                outputVo = getPrepDataModelVarUseList(functionUseMap.get(FunctionTypeEnum.PREP));
            }
        }
        return outputVo;
    }
    /**
     * 数据模型变量被公共方法使用
     * @param varFullPath 路径
     * @param spaceId 空间ID
     * @return KinShipOutputVo
     */
    public KinShipOutputVo getDataModelFunction(String varFullPath,Long spaceId) {
        KinShipOutputVo outputVo = null;
        List<VariableUseVarPathDto> functionQueryList = varProcessFunctionVarService.getFunctionFunctionDataModelVarUseList(spaceId);
        if (!org.springframework.util.CollectionUtils.isEmpty(functionQueryList)) {
            List<VariableUseVarPathDto> varUseList = varProcessVariableVarService.getVarUse(functionQueryList, varFullPath);
            Map<FunctionTypeEnum, List<VariableUseVarPathDto>> functionUseMap = varUseList.stream().collect(Collectors.groupingBy(VariableUseVarPathDto::getFunctionType));
            if (!org.springframework.util.CollectionUtils.isEmpty(varUseList)) {
                outputVo = getFunctionDataModelVarUseList(functionUseMap.get(FunctionTypeEnum.FUNCTION));
            }
        }
        return outputVo;
    }

    /**
     * 数据模型变量被变量模板使用
     * @param varFullPath 路径
     * @param spaceId 空间ID
     * @return KinShipOutputVo
     */
    public KinShipOutputVo getDataModelTemplate(String varFullPath,Long spaceId) {
        KinShipOutputVo outputVo = null;
        List<VariableUseVarPathDto> templateQueryList = varProcessFunctionVarService.getFunctionTempDataModelVarUseList(spaceId);
        if (!org.springframework.util.CollectionUtils.isEmpty(templateQueryList)) {
            List<VariableUseVarPathDto> varUseList = varProcessVariableVarService.getVarUse(templateQueryList, varFullPath);
            Map<FunctionTypeEnum, List<VariableUseVarPathDto>> functionUseMap = varUseList.stream().collect(Collectors.groupingBy(VariableUseVarPathDto::getFunctionType));
            if (!org.springframework.util.CollectionUtils.isEmpty(varUseList)) {
                outputVo = getTemplateDataModelVarUseList(functionUseMap.get(FunctionTypeEnum.TEMPLATE));
            }
        }
        return outputVo;
    }

    /**
     * 数据模型变量被变量清单使用
     * @param varFullPath 路径
     * @param spaceId 空间ID
     * @return KinShipOutputVo
     */
    public KinShipOutputVo getDataModelManifest(String varFullPath,Long spaceId) {
        KinShipOutputVo outputVo = null;
        List<VariableUseVarPathDto> manifestQueryList = varProcessManifestVarService.getManifestVarUseList(spaceId);
        if (!org.springframework.util.CollectionUtils.isEmpty(manifestQueryList)) {
            List<VariableUseVarPathDto> varUseList = varProcessVariableVarService.getVarUse(manifestQueryList, varFullPath);
            if (!org.springframework.util.CollectionUtils.isEmpty(varUseList)) {
                Set<VariableUseVarPathDto> varUseSet = new HashSet<>(varUseList);
                Map<String, List<VariableUseVarPathDto>> varUseMap = varUseSet.stream().collect(Collectors.groupingBy(VariableUseVarPathDto::getName));

                Set<Map.Entry<String, List<VariableUseVarPathDto>>> entries = varUseMap.entrySet();
                List<VariableUseVarPathDto> combinedList = new ArrayList<>();

                for (Map.Entry<String, List<VariableUseVarPathDto>> entry : entries) {
                    List<VariableUseVarPathDto> manifestList = entry.getValue();
                    combinedList.addAll(manifestList);
                }

                if (!CollectionUtils.isEmpty(combinedList)) {
                    List<KinShipOutputVo.NodeInfo> children = combinedList.stream()
                            .map(variable -> KinShipOutputVo.NodeInfo.builder().id(variable.getId()).name(variable.getName()).build())
                            .collect(Collectors.toList());
                    outputVo = KinShipOutputVo.builder().type(KinShipTypeEnum.MANIFEST).children(children).build();
                }

            }
        }
        return outputVo;
    }

    /**
     * 数据模型变量被实时服务使用
     * @param varFullPath 路径
     * @return KinShipOutputVo
     */
    public KinShipOutputVo getDataModelService(String varFullPath) {
        KinShipOutputVo kinShipOutputVo = null;
        LambdaQueryWrapper<VarProcessServiceVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VarProcessServiceVersion::getId,VarProcessServiceVersion::getServiceId,VarProcessServiceVersion::getServiceVersion);
        queryWrapper.eq(VarProcessServiceVersion::getSerialNo,varFullPath);
        List<VarProcessServiceVersion> versionList = varProcessServiceVersionMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(versionList)) {
            Map<Long, String> serviceNameMap = varProcessRealtimeServiceService.list(Wrappers.<VarProcessRealtimeService>lambdaQuery()
                            .select(VarProcessRealtimeService::getId,VarProcessRealtimeService::getServiceName)
                            .in(VarProcessRealtimeService::getId, versionList.stream().map(VarProcessServiceVersion::getServiceId).distinct().collect(Collectors.toList())))
                    .stream().collect(Collectors.toMap(VarProcessRealtimeService::getId, VarProcessRealtimeService::getServiceName));
            List<KinShipOutputVo.NodeInfo> nodeInfoList = new ArrayList<>();
            for (VarProcessServiceVersion versionInfo : versionList) {
                nodeInfoList.add(KinShipOutputVo.NodeInfo.builder().id(versionInfo.getId()).name(serviceNameMap.getOrDefault(versionInfo.getServiceId(), StringPool.EMPTY))
                        .version(versionInfo.getServiceVersion()).build());

            }
            kinShipOutputVo = KinShipOutputVo.builder().type(KinShipTypeEnum.SERVICE).children(nodeInfoList).build();

        }
        return kinShipOutputVo;
    }

    /**
     * 数据模型变量被批量回溯使用
     * @param varFullPath 路径
     * @return KinShipOutputVo
     */
    public KinShipOutputVo getDataModelBacktracking(String varFullPath) {
        KinShipOutputVo kinShipOutputVo = null;
        QueryWrapper<VarProcessBatchBacktracking> batchBacktrackingQueryWrapper = new QueryWrapper<>();
        batchBacktrackingQueryWrapper.select("id", "serial_no", "name", "status");
        List<VarProcessBatchBacktracking> batchBacktrackingDataList = varProcessBatchBacktrackingMapper.selectList(batchBacktrackingQueryWrapper);
        List<VarProcessBatchBacktracking> batchBacktrackingList = new ArrayList<>();
        for (VarProcessBatchBacktracking varProcessBatchBacktracking : batchBacktrackingDataList) {
            if (varProcessBatchBacktracking.getSerialNo() != null && !varProcessBatchBacktracking.getSerialNo().trim().isEmpty() && (varProcessBatchBacktracking.getSerialNo().equals(varFullPath))) {
                batchBacktrackingList.add(varProcessBatchBacktracking);

            }
        }
        if (!CollectionUtils.isEmpty(batchBacktrackingList)) {
            List<KinShipOutputVo.NodeInfo> children = batchBacktrackingList.stream()
                    .map(variable -> KinShipOutputVo.NodeInfo.builder().id(variable.getId()).name(variable.getName()).build())
                    .collect(Collectors.toList());
            kinShipOutputVo = KinShipOutputVo.builder().type(KinShipTypeEnum.BACKTRACKING).children(children).build();
        }
        return kinShipOutputVo;
    }

    /**
     * getUsedVars
     * @param spaceId 空间id
     * @param id 变量id
     * @return list
     */
    public List<KinShipOutputVo> getUsedVars(Long spaceId, Long id) {
        List<VarProcessVariableVar> variableVars = varProcessVariableVarService.list(Wrappers.<VarProcessVariableVar>lambdaQuery()
                .select(VarProcessVariableVar::getVarName, VarProcessVariableVar::getVarPath, VarProcessVariableVar::getVarName)
                .eq(VarProcessVariableVar::getVariableId, id));
        if (!CollectionUtils.isEmpty(variableVars)) {
            List<KinShipOutputVo.NodeInfo> children = variableVars.stream().map(entity -> KinShipOutputVo.NodeInfo.builder().name(entity.getVarName())
                    .code(entity.getVarPath().startsWith(RAW_DATA) ? entity.getVarPath().substring(RAW_DATA.length()) : entity.getVarPath()).hasChildren(false).build()).collect(Collectors.toList());
            KinShipOutputVo outputVo = KinShipOutputVo.builder().type(KinShipTypeEnum.DATA_MODEL).children(children).build();
            return Collections.singletonList(outputVo);
        }
        return new ArrayList<>();
    }

}
