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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.commons.exception.BusinessServiceException;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.ObjectUtils;
import com.wiseco.var.process.app.server.controller.vo.input.DictInsertInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.DictItemQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.DictListInputDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDictDetails;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.converter.VariableDictConverter;
import com.wiseco.var.process.app.server.service.dto.DictTreeDto;
import com.wiseco.var.process.app.server.service.dto.FunctionContentDto;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: xiewu
 */
@Service
public class VarProcessDictBiz {

    @Autowired
    private VarProcessSpaceService       varProcessSpaceService;

    @Autowired
    private VarProcessDictService        varProcessDictService;

    @Autowired
    private VariableDictConverter        variableDictConverter;

    @Autowired
    private VarProcessDictDetailsService varProcessDictDetailsService;

    @Autowired
    private VarProcessDataModelService   varProcessDataModelService;

    @Autowired
    private VarProcessFunctionService varProcessFunctionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    /**
     * 获取字典项的树，给变量模板在生成变量的时候调用
     * @param dictCode 字典的code
     * @return 字典项的树形结构
     */
    public List<DictTreeNode> getDictTree(String dictCode) {
        List<DictTreeNode> allNodes = new ArrayList<>();
        // 1.如果code不为空
        if (!StringUtils.isEmpty(dictCode)) {
            // 1.1 通过dictCode找出dict的id
            Long id = varProcessDictService.getIdByCode(dictCode);
            // 1.2 找出对应的业务字典
            List<VarProcessDictDetails> allDictDetails = varProcessDictDetailsService.list(Wrappers.<VarProcessDictDetails>lambdaQuery()
                            .eq(VarProcessDictDetails::getVarProcessSpaceId, MagicNumbers.ONE)
                            .eq(VarProcessDictDetails::getState, MagicNumbers.ONE)
                            .eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                            .eq(VarProcessDictDetails::getDictId, id));
            for (VarProcessDictDetails item : allDictDetails) {
                DictTreeNode node = new DictTreeNode(item.getCode(), item.getName(), item.getParentCode(), null);
                allNodes.add(node);
            }
        } else {
            // 2.如果传入的code为空，就要筛选出一片森林，首先找出所有的可用业务字典
            List<VarProcessDictDetails> allDictDetails = varProcessDictDetailsService.list(Wrappers.<VarProcessDictDetails>lambdaQuery()
                    .eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                    .eq(VarProcessDictDetails::getVarProcessSpaceId, MagicNumbers.ONE)
                    .eq(VarProcessDictDetails::getState, MagicNumbers.ONE));
            for (VarProcessDictDetails item : allDictDetails) {
                DictTreeNode node = new DictTreeNode(item.getCode(), item.getName(), item.getParentCode(), null);
                allNodes.add(node);
            }
        }
        return allNodes;
    }


    /**
     * insertDict
     * @param inputDto inputDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertDict(DictInsertInputDto inputDto) {
        if (inputDto.getState() == 0) {
            verifyDictDisable(inputDto.getSpaceId(), inputDto.getId());
        }
        List<VarProcessDict> nameList = varProcessDictService.list(new QueryWrapper<VarProcessDict>().lambda()
                .eq(VarProcessDict::getVarProcessSpaceId, inputDto.getSpaceId())
                .eq(VarProcessDict::getName, inputDto.getName())
                .eq(VarProcessDict::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        List<VarProcessDict> codeList = varProcessDictService.list(new QueryWrapper<VarProcessDict>().lambda()
                .eq(VarProcessDict::getVarProcessSpaceId, inputDto.getSpaceId())
                .eq(VarProcessDict::getCode, inputDto.getCode())
                .eq(VarProcessDict::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        VarProcessDict varProcessDict = new VarProcessDict();
        BeanUtils.copyProperties(inputDto, varProcessDict);
        if (null == inputDto.getId()) {
            if (!CollectionUtils.isEmpty(nameList)) {
                throw new BusinessServiceException("字典类型中文名称不能重复！");
            }
            if (!CollectionUtils.isEmpty(codeList)) {
                throw new BusinessServiceException("字典类型编码不能重复！");
            }
            varProcessDict.setVarProcessSpaceId(inputDto.getSpaceId());
            varProcessDict.setCreatedUser(SessionContext.getSessionUser().getUsername());
            varProcessDict.setDeptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode());
            varProcessDict.setUpdatedUser(SessionContext.getSessionUser().getUsername());
            varProcessDictService.save(varProcessDict);
            // 刷新变量空间编辑时间
            refreshVarProcessSpaceUpdateTime(inputDto.getSpaceId());
        } else {
            VarProcessDict varProcessDictById = varProcessDictService.getById(inputDto.getId());
            if (null == varProcessDictById) {
                throw new BusinessServiceException("字典类型id不存在！");
            }
            if (!CollectionUtils.isEmpty(nameList)) {
                long count = nameList.stream().filter(f -> !f.getId().equals(varProcessDictById.getId())).count();
                if (count > 0) {
                    throw new BusinessServiceException("字典类型中文名称不能重复！");
                }
            }
            if (!CollectionUtils.isEmpty(codeList)) {
                long count = codeList.stream().filter(f -> !f.getId().equals(varProcessDictById.getId())).count();
                if (count > 0) {
                    throw new BusinessServiceException("字典类型编码不能重复！");
                }
            }
            if (!varProcessDictById.getName().equals(varProcessDict.getName())
                    || !varProcessDictById.getCode().equals(varProcessDict.getCode())
                    || !varProcessDictById.getState().equals(varProcessDict.getState())) {
                // 字典类型名称, 编码或状态有变化
                varProcessDict.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                varProcessDict.setUpdatedTime(new Date());
                varProcessDictService.updateById(varProcessDict);
                // 刷新变量空间编辑时间
                refreshVarProcessSpaceUpdateTime(inputDto.getSpaceId());
            }
        }
    }

    /**
     * findDictList
     *
     * @param inputDto 输入
     * @return java.util.List
     */
    public List<DictTreeDto> findDictList(DictListInputDto inputDto) {
        //字典类型查询
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        if (ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return new ArrayList<>();
        }
        BeanUtils.copyProperties(roleDataAuthority,inputDto);
        List<DictTreeDto> varProcessDictList = variableDictConverter.getDictList(inputDto);
        if (CollectionUtils.isEmpty(varProcessDictList)) {
            return null;
        }

        //字典项查询
        List<DictTreeDto> varProcessDictDetailsList = variableDictConverter.getDictDetails(inputDto.getSpaceId(), null, null);
        //如果查询到的字典项为空，则直接返回字典类型数据
        if (CollectionUtils.isEmpty(varProcessDictDetailsList)) {
            return varProcessDictList;
        }
        Map<Long, List<DictTreeDto>> dictIdMap = varProcessDictDetailsList.stream().collect(Collectors.groupingBy(DictTreeDto::getDictId));
        for (DictTreeDto dictTreeDto : varProcessDictList) {
            List<DictTreeDto> dictTreeDtoList = dictIdMap.get(dictTreeDto.getId());
            if (!CollectionUtils.isEmpty(dictTreeDtoList)) {
                variableDictConverter.dictTreeDtoSetDictName(dictTreeDtoList, dictTreeDto.getName());
                dictTreeDto.setChildren(dictTreeDtoList);
            }
        }
        if (!CollectionUtils.isEmpty(varProcessDictList) && !StringUtils.isEmpty(inputDto.getNameOrCode())) {
            List<DictTreeDto> list = variableDictConverter.findDictByNameOrCodeCopy(varProcessDictList, inputDto.getNameOrCode());
            return !CollectionUtils.isEmpty(list) ? list : null;
        }
        return varProcessDictList;
    }

    /**
     * getDictItemByCode
     * @param inputDto 输入
     * @return java.util.List
     */
    public List<DictTreeDto> getDictItemByCode(DictItemQueryInputDto inputDto) {
        // 字典类型查询
        VarProcessDict varProcessDict = varProcessDictService.getOne(new QueryWrapper<VarProcessDict>().lambda()
                .eq(VarProcessDict::getVarProcessSpaceId, inputDto.getSpaceId())
                .eq(VarProcessDict::getCode, inputDto.getCode())
                .eq(VarProcessDict::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (null == varProcessDict) {
            // 未查询到字典类型
            return Collections.emptyList();
        }
        // 字典项查询
        List<DictTreeDto> varProcessDictDetailsList = variableDictConverter.getDictDetails(inputDto.getSpaceId(), varProcessDict.getId(), inputDto.getNameOrCode());
        changCreatedUserAndUpdatedUserToChinese(varProcessDictDetailsList);
        return CollectionUtils.isEmpty(varProcessDictDetailsList) ? Collections.emptyList() : varProcessDictDetailsList;
    }

    /**
     * changCreatedUserAndUpdatedUserToChinese
     * @param varProcessDictDetailsList varProcessDictDetailsList
     */
    public void changCreatedUserAndUpdatedUserToChinese(List<DictTreeDto> varProcessDictDetailsList) {
        if (CollectionUtils.isEmpty(varProcessDictDetailsList)) {
            return;
        }
        for (DictTreeDto dictTreeDto : varProcessDictDetailsList) {

            //如果有children 对children的创建者和更新者进行
            Map<String, String> userMap = new HashMap<>(MagicNumbers.EIGHT);
            String createdUserEnglish = userMap.getOrDefault(dictTreeDto.getCreatedUser(), dictTreeDto.getCreatedUser());
            dictTreeDto.setCreatedUser(userService.getFullNameByUserName(createdUserEnglish));

            String updatedUserEnglish = userMap.getOrDefault(dictTreeDto.getUpdatedUser(), dictTreeDto.getUpdatedUser());
            dictTreeDto.setUpdatedUser((userService.getFullNameByUserName(updatedUserEnglish)));

            //如果有children 对children的创建者和更新者进行
            if (!CollectionUtils.isEmpty(dictTreeDto.getChildren())) {
                changCreatedUserAndUpdatedUserToChinese(dictTreeDto.getChildren());
            }
        }
    }

    /**
     * 通过Id删除字典
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictById(Long id) {
        VarProcessDict varProcessDict = varProcessDictService.getById(id);
        if (null == varProcessDict) {
            throw new BusinessServiceException("字典类型id数据不存在！");
        }
        varProcessDictService.update(new UpdateWrapper<VarProcessDict>().lambda()
                .eq(VarProcessDict::getId, id)
                .eq(VarProcessDict::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .set(VarProcessDict::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()));
        varProcessDictDetailsService.update(new UpdateWrapper<VarProcessDictDetails>().lambda()
                .eq(VarProcessDictDetails::getVarProcessSpaceId, varProcessDict.getVarProcessSpaceId())
                .eq(VarProcessDictDetails::getDictId, id)
                .eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .set(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()));
        // 刷新变量空间编辑时间
        refreshVarProcessSpaceUpdateTime(varProcessDict.getVarProcessSpaceId());
    }

    /**
     * verifyDictDeletion
     * @param spaceId spaceId
     * @param varProcessDictId varProcessDictId
     * @return java.lang.String
     */
    public String verifyDictDeletion(Long spaceId, Long varProcessDictId) {
        VarProcessDict varProcessDict = varProcessDictService.getById(varProcessDictId);
        // 1. 查询指定字典类型是否被指定数据模型引用
        // 获取数据模型实例对象
        List<VarProcessDataModel> list = varProcessDataModelService.list(
                new QueryWrapper<VarProcessDataModel>().lambda()
                        .eq(VarProcessDataModel::getVarProcessSpaceId, spaceId)
        );
        // 寻找数据模型中的字典类型编码
        List<String> enumNameList = new ArrayList<>();
        for (VarProcessDataModel varProcessDataModel : list) {
            // 获取数据模型树形结构
            JSONObject jsonObject = JSON.parseObject(varProcessDataModel.getContent(), Feature.OrderedField);
            DomainModelTree varProcessModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(jsonObject);
            variableDictConverter.findDataModelVarEnumName(varProcessModelTree, enumNameList);
        }

        // 2. 判断字典是不是被function使用
        findDictWithFunction(spaceId, enumNameList);

        // 寻找数据模型中的字典类型编码 (enumName)
        // 获取字典类型编码 (数据变量树形结构的 enumName 字段)
        String enumName = varProcessDict.getCode();
        //判断字典类型有没有被使用
        if (enumNameList.contains(enumName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.CONFIG_DICT_ALREADY_USED, "该字典类型已被使用，不允许删除。");
        } else {
            //字典类型没有被使用，判断字典类型有没有字典项
            DictItemQueryInputDto  dictItemQueryInputDto = new DictItemQueryInputDto();
            dictItemQueryInputDto.setCode(varProcessDict.getCode());
            dictItemQueryInputDto.setSpaceId(spaceId);
            if (getDictItemByCode(dictItemQueryInputDto).size() > 0) {
                return "该字典类型下已有字典项，确认删除？";
            } else {
                return "确认删除？";
            }
        }
    }

    /**
     * 刷新变量空间编辑时间
     * @param varProcessSpaceId 变量空间 ID
     */
    private void refreshVarProcessSpaceUpdateTime(Long varProcessSpaceId) {
        varProcessSpaceService.update(Wrappers.<VarProcessSpace>lambdaUpdate()
                .eq(VarProcessSpace::getId, varProcessSpaceId)
                .set(VarProcessSpace::getUpdatedTime, new Date()));
    }

    /**
     * 校验字典是否可用
     * @param spaceId 变量空间Id
     * @param varProcessDictId 字典类型Id
     */
    public void verifyDictDisable(Long spaceId, Long varProcessDictId) {
        if (varProcessDictId == null) {
            return;
        }
        // 1. 查询指定字典类型是否被指定数据模型引用
        // 获取数据模型实例对象
        List<VarProcessDataModel> list = varProcessDataModelService.list(
                new QueryWrapper<VarProcessDataModel>().lambda()
                        .eq(VarProcessDataModel::getVarProcessSpaceId, spaceId)
        );
        // 寻找数据模型中的字典类型编码
        List<String> enumNameList = new ArrayList<>();
        for (VarProcessDataModel varProcessDataModel : list) {
            // 获取数据模型树形结构
            JSONObject jsonObject = JSONObject.parseObject(varProcessDataModel.getContent(), Feature.OrderedField);
            DomainModelTree varProcessModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(jsonObject);
            variableDictConverter.findDataModelVarEnumName(varProcessModelTree, enumNameList);
        }

        // 2. 判断字典是不是被function使用
        findDictWithFunction(spaceId, enumNameList);

        // 寻找数据模型中的字典类型编码 (enumName)
        // 获取字典类型编码 (数据变量树形结构的 enumName 字段)
        VarProcessDict varProcessDict = varProcessDictService.getById(varProcessDictId);
        String enumName = varProcessDict.getCode();
        if (enumNameList.contains(enumName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.CONFIG_DICT_ALREADY_USED, "该字典类型已被使用，不允许停用。");
        }
    }

    /**
     * 找function中绑了字典的项目
     * @param spaceId 项目id
     * @param enumNameList 已绑字典项目
     */
    private void findDictWithFunction(Long spaceId, List<String> enumNameList) {
        List<VarProcessFunction> functionList = varProcessFunctionService.list(new QueryWrapper<VarProcessFunction>().lambda()
                .select(VarProcessFunction::getId, VarProcessFunction::getContent)
                .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        for (VarProcessFunction function : functionList) {
            if (StringUtils.isEmpty(function.getContent())) {
                continue;
            }
            FunctionContentDto functionContentDto = JSONObject.parseObject(function.getContent(), FunctionContentDto.class);
            if (functionContentDto.getBaseData() == null) {
                continue;
            }
            List<FunctionContentDto.LocalVar> parameters = functionContentDto.getBaseData().getDataModel().getParameters();
            if (CollectionUtils.isEmpty(parameters)) {
                continue;
            }
            parameters.forEach(x -> {
                if (!StringUtils.isEmpty(x.getDictCode())) {
                    enumNameList.add(x.getDictCode());
                }
            });
        }
    }

    /**
     * 构建出一棵树
     * @param nodeList 所有的结点
     * @param node 一棵树
     * @return 一棵树
     */
    public DictTreeNode buildTree(List<DictTreeNode> nodeList, DictTreeNode node) {
        // 1.保存结点的子结点
        List<DictTreeNode> childTree = new ArrayList<>();
        // 2.递归构建
        for (DictTreeNode item : nodeList) {
            if (item.getParentCode().equals(node.getCode())) {
                childTree.add(buildTree(nodeList, item));
            }
        }
        node.setChildren(childTree);
        return node;
    }

    /**
     * 构建出一片森林
     * @param rootNodes 所有的根结点
     * @param allNodes 所有的结点
     * @return 一片森林
     */
    public List<DictTreeNode> buildForest(List<DictTreeNode> rootNodes, List<DictTreeNode> allNodes) {
        // 1.所有的树根结点
        List<DictTreeNode> roots = new ArrayList<>();
        // 2.根据根结点建树
        for (DictTreeNode node : rootNodes) {
            node = buildTree(allNodes, node);
            roots.add(node);
        }
        return roots;
    }

    /**
     * 获取所有的根结点，当且仅当code为空时调用
     * @param allNodes 所有的结点
     * @return 所有的根结点
     */
    private List<DictTreeNode> getRootNodes(List<DictTreeNode> allNodes) {
        // 1.保存所有的根结点数据
        List<DictTreeNode> rootNodeList = new ArrayList<>();
        // 2.查询出每一个根结点
        for (DictTreeNode node : allNodes) {
            if (StringUtils.isEmpty(node.getParentCode())) {
                rootNodeList.add(node);
            }
        }
        return rootNodeList;
    }

    /**
     * 通过深度优先遍历，设置子树为[]的结点为null
     * @param root 根结点
     */
    public void dfsSetChildren(DictTreeNode root) {
        // 1.先设置根结点
        if (StringUtils.isEmpty(root.getParentCode())) {
            root.setParentCode(null);
        }
        List<DictTreeNode> children = root.getChildren();
        if (children.size() == MagicNumbers.ZERO) {
            root.setChildren(null);
            return;
        }
        // 2.深度有限遍历子结点
        for (DictTreeNode node : children) {
            dfsSetChildren(node);
        }
    }

    /**
     * 字典的树形结构
     */
    @Data
    @AllArgsConstructor
    public static class DictTreeNode {
        /**
         * 字典项的编码
         */
        private String code;
        /**
         * 字典项的名称
         */
        private String name;
        /**
         * 父code
         */
        private String parentCode;
        /**
         * 孩子结点
         */
        private List<DictTreeNode> children;
    }
}
