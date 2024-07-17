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
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.security.SessionUser;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.SceneListSimpleOutputVO;
import com.wiseco.var.process.app.server.controller.vo.input.SceneListInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.SceneSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.SceneDetailOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.SceneListOutputVO;
import com.wiseco.var.process.app.server.enums.SceneStateEnum;
import com.wiseco.var.process.app.server.enums.SceneVarRoleEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.repository.entity.VarProcessScene;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSceneEvent;
import com.wiseco.var.process.app.server.service.common.DataSourceService;
import com.wiseco.var.process.app.server.service.common.DeptService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.SceneVarRoleDto;
import com.wisecotech.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wiseco.var.process.app.server.exception.VariableMgtErrorCode.SCENE_CODE_EXISTS;
import static com.wiseco.var.process.app.server.exception.VariableMgtErrorCode.SCENE_DATA_MODEL_BOUND;
import static com.wiseco.var.process.app.server.exception.VariableMgtErrorCode.SCENE_DATA_SOURCE_BOUND;
import static com.wiseco.var.process.app.server.exception.VariableMgtErrorCode.SCENE_INPUT_MISS;
import static com.wiseco.var.process.app.server.exception.VariableMgtErrorCode.SCENE_NAME_EXISTS;

@Slf4j
@Service
public class VarProcessSceneBiz {

    @Autowired
    private VarProcessSceneService varProcessSceneService;

    @Autowired
    private VarProcessSceneEventService varProcessSceneEventService;

    @Autowired
    private VarProcessVariableSceneService varProcessVariableSceneService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private VariableCommonBiz variableCommonBiz;

    /**
     * 保存场景
     *
     * @param inputDto 入参dto
     * @return 场景id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveScene(SceneSaveInputVO inputDto) {

        saveCheck(inputDto);

        SessionUser user = SessionContext.getSessionUser();

        VarProcessScene varProcessScene = new VarProcessScene();
        BeanUtils.copyProperties(inputDto, varProcessScene);
        varProcessScene.setVarRoles(JSON.toJSONString(inputDto.getVarRoles()));
        varProcessScene.setUpdatedUser(user.getUsername());
        if (inputDto.getId() == null) {
            varProcessScene.setCreatedUser(user.getUsername());
            varProcessScene.setDeptCode(user.getUser().getDepartment().getCode());
            varProcessScene.setState(SceneStateEnum.ENABLED);
        }
        varProcessScene.setUpdatedTime(new Date());
        varProcessSceneService.saveOrUpdate(varProcessScene);

        List<SceneSaveInputVO.SceneEventDto> events = inputDto.getEvents();
        Set<String> eventNameSet = events.stream().map(SceneSaveInputVO.SceneEventDto::getEventName).collect(Collectors.toSet());
        Map<Boolean, List<VarProcessSceneEvent>> oldEventMap = varProcessSceneEventService.list(Wrappers.<VarProcessSceneEvent>lambdaQuery().eq(VarProcessSceneEvent::getSceneId, inputDto.getId()))
                .stream().collect(Collectors.groupingBy(item -> eventNameSet.contains(item.getEventName())));
        varProcessSceneEventService.removeByIds(oldEventMap.get(false));
        Map<String,Long> updateEvents = oldEventMap.getOrDefault(true,new ArrayList<>()).stream().collect(Collectors.toMap(VarProcessSceneEvent::getEventName,VarProcessSceneEvent::getId));
        List<VarProcessSceneEvent> varProcessSceneEvents = events.stream().map(item -> {
            VarProcessSceneEvent event = new VarProcessSceneEvent();
            event.setId(updateEvents.getOrDefault(item.getEventName(),null));
            event.setEventName(item.getEventName());
            event.setSceneId(varProcessScene.getId());
            event.setSceneCmpSymbol(item.getSceneCmpSymbol());
            event.setCodeValue(JSON.toJSONString(item.getCodeValue()));
            event.setUpdatedTime(new Date());
            return event;
        }).collect(Collectors.toList());
        varProcessSceneEventService.saveOrUpdateBatch(varProcessSceneEvents);

        return varProcessScene.getId();
    }

    /**
     * 保存场景校验
     *
     * @param inputDto 入参dto
     */
    private void saveCheck(SceneSaveInputVO inputDto) {
        long count = varProcessSceneService.count(Wrappers.<VarProcessScene>lambdaQuery().eq(VarProcessScene::getName, inputDto.getName())
                .eq(VarProcessScene::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).ne(inputDto.getId() != null, VarProcessScene::getId, inputDto.getId()));
        if (count != 0) {
            throw new VariableMgtBusinessServiceException(SCENE_NAME_EXISTS);
        }
        count = varProcessSceneService.count(Wrappers.<VarProcessScene>lambdaQuery().eq(VarProcessScene::getCode, inputDto.getCode())
                .eq(VarProcessScene::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).ne(inputDto.getId() != null, VarProcessScene::getId, inputDto.getId()));
        if (count != 0) {
            throw new VariableMgtBusinessServiceException(SCENE_CODE_EXISTS);
        }
        count = varProcessSceneService.count(Wrappers.<VarProcessScene>lambdaQuery().eq(VarProcessScene::getDataSource, inputDto.getDataSource())
                .eq(VarProcessScene::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).ne(inputDto.getId() != null, VarProcessScene::getId, inputDto.getId()));
        if (count != 0) {
            throw new VariableMgtBusinessServiceException(SCENE_DATA_SOURCE_BOUND);
        }
        count = varProcessSceneService.count(Wrappers.<VarProcessScene>lambdaQuery().eq(VarProcessScene::getDataModelName, inputDto.getDataModelName())
                .eq(VarProcessScene::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).ne(inputDto.getId() != null, VarProcessScene::getId, inputDto.getId()));
        if (count != 0) {
            throw new VariableMgtBusinessServiceException(SCENE_DATA_MODEL_BOUND);
        }

        Set<String> varSet = new HashSet<>();
        for (SceneVarRoleEnum key : SceneVarRoleEnum.values()) {
            Map<SceneVarRoleEnum, List<String>> varRoles = inputDto.getVarRoles();
            if (!varRoles.containsKey(key) || CollectionUtils.isEmpty(varRoles.get(key))) {
                throw new VariableMgtBusinessServiceException(SCENE_INPUT_MISS, "变量角色定义不完全，请检查");
            }
            List<String> varNames = varRoles.get(key);
            if (SceneVarRoleEnum.MATCH_DIMENSION != key && varNames.size() > 1) {
                throw new VariableMgtBusinessServiceException(MessageFormat.format("{0}仅支持单选~", key.getDesc()));
            }
            varNames.forEach(varName -> {
                if (varSet.contains(varName)) {
                    throw new VariableMgtBusinessServiceException(MessageFormat.format("变量{0}配置多个变量角色，请检查", varName));
                } else {
                    varSet.add(varName);
                }
            });
        }

        Set<String> eventNameSet = inputDto.getEvents().stream().map(SceneSaveInputVO.SceneEventDto::getEventName).collect(Collectors.toSet());
        if (eventNameSet.size() < inputDto.getEvents().size()) {
            throw new VariableMgtBusinessServiceException("事件名称重复");
        }
    }

    /**
     * 查看详情
     *
     * @param id 场景id
     * @return 出参dto
     */
    public SceneDetailOutputVO detail(Long id) {
        VarProcessScene varProcessScene = varProcessSceneService.getById(id);
        List<VarProcessSceneEvent> eventList = varProcessSceneEventService.list(Wrappers.<VarProcessSceneEvent>lambdaQuery().eq(VarProcessSceneEvent::getSceneId, id));
        return SceneDetailOutputVO.builder().name(varProcessScene.getName()).code(varProcessScene.getCode()).state(varProcessScene.getState())
                .dataSource(varProcessScene.getDataSource()).dataModelName(varProcessScene.getDataModelName())
                .varRoles(JSON.parseObject(varProcessScene.getVarRoles(), new TypeReference<Map<SceneVarRoleEnum, List<java.lang.String>>>() {
                }))
                .events(eventList.stream().map(item -> SceneDetailOutputVO.SceneEventDto.builder().eventName(item.getEventName()).sceneCmpSymbol(item.getSceneCmpSymbol())
                        .codeValue(JSON.parseObject(item.getCodeValue(), new TypeReference<List<String>>() {
                        })).build()).collect(Collectors.toList())).build();
    }

    /**
     * 场景列表
     *
     * @param inputDto 入参dto
     * @return page
     */
    public Page<SceneListOutputVO> list(SceneListInputDto inputDto) {

        Page<SceneListOutputVO> outputPage = new Page<>();

        LambdaQueryWrapper<VarProcessScene> queryWrapper = Wrappers.<VarProcessScene>lambdaQuery()
                .select(VarProcessScene::getId, VarProcessScene::getName, VarProcessScene::getCode, VarProcessScene::getState, VarProcessScene::getDataSource, VarProcessScene::getDataModelName,
                        VarProcessScene::getCreatedUser, VarProcessScene::getUpdatedUser, VarProcessScene::getCreatedTime, VarProcessScene::getUpdatedTime, VarProcessScene::getDeptCode)
                .eq(VarProcessScene::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .eq(inputDto.getState() != null, VarProcessScene::getState, inputDto.getState())
                .eq(!StringUtils.isEmpty(inputDto.getDeptCode()), VarProcessScene::getDeptCode, inputDto.getDeptCode())
                .like(!StringUtils.isEmpty(inputDto.getDataModelName()), VarProcessScene::getDataModelName, inputDto.getDataModelName())
                .orderByDesc(VarProcessScene::getUpdatedTime);
        if (!StringUtils.isEmpty(inputDto.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper.like(VarProcessScene::getName, inputDto.getKeyword()).or().like(VarProcessScene::getCode, inputDto.getKeyword()));
        }
        Page<VarProcessScene> queryResultpage = varProcessSceneService.page(new Page<>(inputDto.getCurrentNo(), inputDto.getSize()), queryWrapper);

        List<String> userNames = queryResultpage.getRecords().stream().flatMap(scene -> Stream.of(scene.getUpdatedUser(), scene.getCreatedUser())).collect(Collectors.toList());
        Map<String, String> fullNameMap = userService.findFullNameMapByUserNames(userNames);
        Map<String, String> deptCodeNameMap = deptService.findDeptMapByDeptCodes(queryResultpage.getRecords().stream().map(VarProcessScene::getDeptCode).collect(Collectors.toList()));
        //todo 是否使用 & 数据源
        Set<Long> usedSet = varProcessVariableSceneService.findUsedScenes();
        Map<String, String> dataSourceNameMap = new HashMap<>(MagicNumbers.EIGHT);
        BeanUtils.copyProperties(queryResultpage, outputPage);
        outputPage.setRecords(queryResultpage.getRecords().stream().map(item -> {
            SceneListOutputVO outputVO = new SceneListOutputVO();
            BeanUtils.copyProperties(item, outputVO);
            outputVO.setCreatedUser(fullNameMap.getOrDefault(item.getCreatedUser(), StringPool.EMPTY));
            outputVO.setUpdatedUser(fullNameMap.getOrDefault(item.getUpdatedUser(), StringPool.EMPTY));
            outputVO.setCreatedDept(deptCodeNameMap.getOrDefault(item.getDeptCode(), StringPool.EMPTY));
            outputVO.setUsed(usedSet.contains(item.getId()));
            outputVO.setDataSourceName(dataSourceNameMap.getOrDefault(item.getDataSource(), item.getDataSource()));
            return outputVO;
        }).collect(Collectors.toList()));
        return outputPage;
    }

    /**
     * 更新场景状态
     *
     * @param id         场景id
     * @param actionType 操作类型
     * @return 场景id
     */
    public Long updateState(Long id, Integer actionType) {
        updateStateCheck(id, actionType);
        VarProcessScene varProcessScene = varProcessSceneService.getById(id);
        varProcessScene.setState(actionType == 1 ? SceneStateEnum.ENABLED : SceneStateEnum.DISABLED);
        varProcessSceneService.updateById(varProcessScene);
        return varProcessScene.getId();
    }

    /**
     * 更新状态校验
     *
     * @param id         场景id
     * @param actionType 操作类型
     * @return 场景id
     */
    public String updateStateCheck(Long id, Integer actionType) {
        return MessageFormat.format("确认{0}该场景？", (actionType == 1 ? SceneStateEnum.ENABLED.getDesc() : SceneStateEnum.DISABLED.getDesc()));
    }

    /**
     * 删除场景
     *
     * @param id 场景id
     * @return 删除成功
     */
    public String deleteScene(Long id) {
        deleteSceneCheck(id);
        VarProcessScene varProcessScene = varProcessSceneService.getById(id);
        varProcessScene.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
        varProcessSceneService.updateById(varProcessScene);

        varProcessSceneEventService.remove(Wrappers.<VarProcessSceneEvent>lambdaQuery().eq(VarProcessSceneEvent::getSceneId, id));
        return "删除成功";
    }

    /**
     * 删除场景校验
     *
     * @param id 场景id
     * @return 确认删除？
     */
    public String deleteSceneCheck(Long id) {
        if (0 != varProcessVariableSceneService.countUseVariables(id)) {
            throw new VariableMgtBusinessServiceException("该场景已被使用，不允许删除");
        }
        return "确认删除？";
    }

    /**
     * 获取MQ数据源
     *
     * @return list
     */
    public List<String> findDataSources() {
        return dataSourceService.findMqDataSources();
    }

    /**
     * 获取变量角色枚举
     *
     * @return list
     */
    public List<SceneVarRoleDto> findVarRoleEnums() {
        return Arrays.stream(SceneVarRoleEnum.values()).map(item -> SceneVarRoleDto.builder().varRoleEnum(item).build()).collect(Collectors.toList());
    }

    /**
     * 获取场景list
     * @return list
     */
    public List<SceneListSimpleOutputVO> findEnabledSceneList() {
        return varProcessSceneService.list(Wrappers.<VarProcessScene>lambdaQuery()
                        .select(VarProcessScene::getId,VarProcessScene::getName,VarProcessScene::getDataModelName)
                .eq(VarProcessScene::getDeleteFlag,DeleteFlagEnum.USABLE.getCode()).eq(VarProcessScene::getState,SceneStateEnum.ENABLED))
                .stream().map(item -> SceneListSimpleOutputVO.builder().sceneId(item.getId()).sceneName(item.getName()).dataModelName(item.getDataModelName()).build()).collect(Collectors.toList());
    }

    /**
     * 获取事件list
     * @param sceneId 场景id
     * @return list
     */
    public List<SceneListSimpleOutputVO.EventOutputDto> findEventListOfScene(Long sceneId) {
        return varProcessSceneEventService.list(Wrappers.<VarProcessSceneEvent>lambdaQuery()
                .select(VarProcessSceneEvent::getId,VarProcessSceneEvent::getEventName).eq(VarProcessSceneEvent::getSceneId, sceneId))
                .stream().map(item -> SceneListSimpleOutputVO.EventOutputDto.builder().eventId(item.getId()).eventName(item.getEventName()).build()).collect(Collectors.toList());
    }

    /**
     * 根据场景id查询匹配维度
     * @param sceneId 场景id
     * @return list
     */
    public List<SceneListSimpleOutputVO.MatchDimensionOutputDto> findMatchDimensionsOfScene(Long sceneId) {
        VarProcessScene scene = varProcessSceneService.getById(sceneId);
        if (scene == null) {
            return new ArrayList<>();
        }
        Map<SceneVarRoleEnum, List<String>> varRoleMap = JSON.parseObject(scene.getVarRoles(), new TypeReference<Map<SceneVarRoleEnum, List<String>>>() {
        });
        List<String> matchDimensions = varRoleMap.get(SceneVarRoleEnum.MATCH_DIMENSION);
        List<DomainDataModelTreeDto> treeDto = variableCommonBiz.findDatModelBasicVars(scene.getDataModelName(), SceneVarRoleEnum.MATCH_DIMENSION.getVarTypeList().stream().map(DataVariableTypeEnum::getMessage).collect(Collectors.toList()));

        ArrayList<SceneListSimpleOutputVO.MatchDimensionOutputDto> outputDtos = new ArrayList<>();
        Queue<DomainDataModelTreeDto> queue = new LinkedList<>(treeDto);
        while (!queue.isEmpty()) {
            DomainDataModelTreeDto poll = queue.poll();
            if (matchDimensions.contains(poll.getValue())) {
                outputDtos.add(SceneListSimpleOutputVO.MatchDimensionOutputDto.builder().matchDimension(poll.getValue()).matchDimensionLabel(poll.getDescribe()).build());
            }
            if (!CollectionUtils.isEmpty(poll.getChildren())) {
                queue.addAll(poll.getChildren());
            }
        }
        return outputDtos;
    }
}
