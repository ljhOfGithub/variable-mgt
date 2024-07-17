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
package com.wiseco.var.process.app.server.service.converter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.input.DictListInputDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDictDetails;
import com.wiseco.var.process.app.server.service.VarProcessDictDetailsService;
import com.wiseco.var.process.app.server.service.VarProcessDictService;
import com.wiseco.var.process.app.server.service.dto.DictTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: xiewu
 */
@Component
@Slf4j
public class VariableDictConverter {

    @Autowired
    private VarProcessDictService varProcessDictService;

    @Autowired
    private VarProcessDictDetailsService varProcessDictDetailsService;


    /**
     * 获取字典项信息
     * @param spaceId 变量空间Id
     * @param dictId 字典Id
     * @param nameAndCode 名称或者code
     * @return List
     */
    public List<DictTreeDto> getDictDetails(Long spaceId, Long dictId, String nameAndCode) {
        //查询字典数据
        LambdaQueryWrapper<VarProcessDictDetails> detailsLambdaQueryWrapper = new QueryWrapper<VarProcessDictDetails>().lambda().orderByDesc(VarProcessDictDetails::getUpdatedTime).eq(VarProcessDictDetails::getVarProcessSpaceId, spaceId).eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());

        if (null != dictId) {
            detailsLambdaQueryWrapper.eq(VarProcessDictDetails::getDictId, dictId);
        }

        if (!StringUtils.isEmpty(nameAndCode)) {
            detailsLambdaQueryWrapper.nested(queryWrapper -> queryWrapper.like(VarProcessDictDetails::getName, nameAndCode).or().like(VarProcessDictDetails::getCode, nameAndCode));
        }

        List<VarProcessDictDetails> varProcessDictDetailsList = varProcessDictDetailsService.list(detailsLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(varProcessDictDetailsList)) {
            return null;
        }

        Map<String, String> userMap = new HashMap<>(MagicNumbers.EIGHT);
        for (VarProcessDictDetails varProcessDictDetails : varProcessDictDetailsList) {
            if (!StringUtils.isEmpty(varProcessDictDetails.getCreatedUser())) {
                varProcessDictDetails.setCreatedUser(userMap.getOrDefault(varProcessDictDetails.getCreatedUser(), varProcessDictDetails.getCreatedUser()));
            }

            if (!StringUtils.isEmpty(varProcessDictDetails.getUpdatedUser())) {
                varProcessDictDetails.setUpdatedUser(userMap.getOrDefault(varProcessDictDetails.getUpdatedUser(), varProcessDictDetails.getUpdatedUser()));
            }
        }

        List<DictTreeDto> listNewOutputDtoList = new ArrayList<>();
        //这里避免有不同字典类型之前有重复编码的情况，需要先按照字典类型id进行分组
        Map<Long, List<VarProcessDictDetails>> dictIdMap = varProcessDictDetailsList.stream().collect(Collectors.groupingBy(VarProcessDictDetails::getDictId));
        String parent = "";
        for (Map.Entry<Long, List<VarProcessDictDetails>> longListEntry : dictIdMap.entrySet()) {
            //给空父级编码赋值，作为分组key
            for (VarProcessDictDetails varProcessDictDetails : longListEntry.getValue()) {
                if (StringUtils.isEmpty(varProcessDictDetails.getParentCode())) {
                    varProcessDictDetails.setParentCode(parent);
                }
            }
            Map<String, List<VarProcessDictDetails>> varProcessDictDetailsMapList = longListEntry.getValue().stream().collect(Collectors.groupingBy(VarProcessDictDetails::getParentCode));
            List<VarProcessDictDetails> levelOneVarProcessDictDetailsList = varProcessDictDetailsMapList.get(parent);
            if (CollectionUtils.isEmpty(levelOneVarProcessDictDetailsList)) {
                continue;
            }
            varProcessDictDetailsMapList.remove(parent);
            List<DictTreeDto> list = varProcessDictDetailsConvertDictListNewOutputDto(levelOneVarProcessDictDetailsList);
            if (!CollectionUtils.isEmpty(varProcessDictDetailsMapList)) {
                packageDictListNewOutputDtoTree(list, varProcessDictDetailsMapList);
            }
            listNewOutputDtoList.addAll(list);
        }
        return listNewOutputDtoList;
    }

    /**
     * 组装新的dictTree
     * @param listNewOutputDtoList  字典树列表
     * @param varProcessDictDetailsMapList 字典详情
     */
    public void packageDictListNewOutputDtoTree(List<DictTreeDto> listNewOutputDtoList, Map<String, List<VarProcessDictDetails>> varProcessDictDetailsMapList) {
        //等于null说明已经组装完成
//        if (CollectionUtils.isEmpty(varProcessDictDetailsMapList)) {
//            return;
//        }
        //这里是为了判断listNewOutputDtoList在map中是否有子集，如果有继续走，否则结束，为了避免数据错乱的情况下发生栈溢出
        long count = listNewOutputDtoList.stream().filter(f -> !CollectionUtils.isEmpty(varProcessDictDetailsMapList.get(f.getCode()))).count();

        if (count > 0) {
            for (DictTreeDto dictTreeDto : listNewOutputDtoList) {
                List<VarProcessDictDetails> varProcessDictDetailsList = varProcessDictDetailsMapList.get(dictTreeDto.getCode());
                if (!CollectionUtils.isEmpty(varProcessDictDetailsList)) {
                    List<DictTreeDto> children = varProcessDictDetailsConvertDictListNewOutputDto(varProcessDictDetailsList);
                    dictTreeDto.setChildren(children);
                    varProcessDictDetailsMapList.remove(dictTreeDto.getCode());
                }
                //继续递归寻找子集，一直找到没有子集为止
                if (!CollectionUtils.isEmpty(dictTreeDto.getChildren())) {
                    packageDictListNewOutputDtoTree(dictTreeDto.getChildren(), varProcessDictDetailsMapList);
                }
            }
        } else {
            return;
        }


    }

    /**
     * 给子典型设置字典类型名称
     *
     * @param dictTreeDtoList 字典树的list
     * @param dictName 字典名称
     */
    public void dictTreeDtoSetDictName(List<DictTreeDto> dictTreeDtoList, String dictName) {
        if (CollectionUtils.isEmpty(dictTreeDtoList)) {
            return;
        }
        for (DictTreeDto dictTreeDto : dictTreeDtoList) {
            dictTreeDto.setDictName(dictName);
            if (!CollectionUtils.isEmpty(dictTreeDto.getChildren())) {
                dictTreeDtoSetDictName(dictTreeDto.getChildren(), dictName);
            }
        }
    }

    /**
     * varProcessDictDetailsConvertDictListNewOutputDto
     *
     * @param varProcessDictDetailsList 字典list
     * @return 字典树的list
     */
    public List<DictTreeDto> varProcessDictDetailsConvertDictListNewOutputDto(List<VarProcessDictDetails> varProcessDictDetailsList) {
        List<DictTreeDto> list = new ArrayList<>();
        for (VarProcessDictDetails varProcessDictDetails : varProcessDictDetailsList) {
            DictTreeDto dictTreeDto = new DictTreeDto();
            BeanUtils.copyProperties(varProcessDictDetails, dictTreeDto);
            //如果修改人等于null，则上级编辑人是创建人
            if (StringUtils.isEmpty(dictTreeDto.getUpdatedUser())) {
                dictTreeDto.setUpdatedUser(varProcessDictDetails.getCreatedUser());
            }
            list.add(dictTreeDto);
        }
        return list;
    }

    /**
     * getDictList
     *
     * @param inputDto 输入实体对象
     * @return 字典树的list
     */
    public List<DictTreeDto> getDictList(DictListInputDto inputDto) {
        List<VarProcessDict> varProcessDictList = varProcessDictService.getAllList(inputDto);
        if (CollectionUtils.isEmpty(varProcessDictList)) {
            return null;
        }

        Map<String, String> userMap = new HashMap<>(MagicNumbers.EIGHT);
        List<DictTreeDto> list = new ArrayList<>();
        for (VarProcessDict varProcessDict : varProcessDictList) {
            DictTreeDto dictTreeDto = new DictTreeDto();
            BeanUtils.copyProperties(varProcessDict, dictTreeDto);
            //如果修改人等于null，则上级编辑人是创建人
            if (StringUtils.isEmpty(dictTreeDto.getUpdatedUser())) {
                dictTreeDto.setUpdatedUser(varProcessDict.getCreatedUser());
            }
            if (!StringUtils.isEmpty(dictTreeDto.getCreatedUser())) {
                dictTreeDto.setCreatedUser(userMap.getOrDefault(dictTreeDto.getCreatedUser(), dictTreeDto.getCreatedUser()));
            }

            if (!StringUtils.isEmpty(dictTreeDto.getUpdatedUser())) {
                dictTreeDto.setUpdatedUser(userMap.getOrDefault(dictTreeDto.getUpdatedUser(), dictTreeDto.getUpdatedUser()));
            }
            list.add(dictTreeDto);
        }
        return list;
    }

    /**
     * findDictByNameOrCodeCopy
     *
     * @param varProcessDictList 字典树list
     * @param nameOrCode 名称或者code
     * @return 字典树list
     */
    public List<DictTreeDto> findDictByNameOrCodeCopy(List<DictTreeDto> varProcessDictList, String nameOrCode) {
        List<DictTreeDto> list = new ArrayList<>();
        for (DictTreeDto varProcessDictTreeDto : varProcessDictList) {
            varProcessDictTreeDto.setValue(varProcessDictTreeDto.getCode());
            setDictTreeDtoValue(varProcessDictTreeDto);
            List<DictTreeDto> listInput = new ArrayList<>();
            listInput.add(varProcessDictTreeDto);
            //这里如果不等于空，匹配出来只包含本身和子集
            List<DictTreeDto> dictTreeDto = new ArrayList<>();
            varProcessDictTreeListByNameOrCodeCopy(listInput, nameOrCode, dictTreeDto);
            if (!CollectionUtils.isEmpty(dictTreeDto)) {
                String[] split = dictTreeDto.get(0).getValue().split("\\.");
                if (null == split) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "字典树路径异常！");
                }
                if (split.length == 1) {
                    list.add(varProcessDictTreeDto);
                } else {
                    List<DictTreeDto> resultList = new ArrayList<>();
                    //寻找匹配到的字典项的所有上级节点
                    for (int i = 0; i < split.length - 1; i++) {
                        varProcessDictTreeDto = findByCodeDictTreeDto(varProcessDictTreeDto, split[i]);
                        if (null == varProcessDictTreeDto) {
                            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "根据路径寻找字典树异常！");
                        }
                        resultList.add(varProcessDictTreeDto);
                    }
                    if (!CollectionUtils.isEmpty(resultList)) {
                        //把寻找到的上级节点组装起来
                        for (int i = 0; i < resultList.size() - 1; i++) {
                            List<DictTreeDto> childrenList = new ArrayList<>();
                            childrenList.add(resultList.get(i + 1));
                            resultList.get(i).setChildren(childrenList);
                        }
                        //把寻找到的节点信息赋给它的父节点
                        resultList.get(resultList.size() - 1).setChildren(dictTreeDto);
                        list.add(resultList.get(0));
                    }
                }
            }
        }
        return list;
    }

    private void setDictTreeDtoValue(DictTreeDto dictTreeDto) {
        if (CollectionUtils.isEmpty(dictTreeDto.getChildren())) {
            return;
        }
        for (DictTreeDto children : dictTreeDto.getChildren()) {
            children.setValue(dictTreeDto.getValue() + "." + children.getCode());
            if (!CollectionUtils.isEmpty(children.getChildren())) {
                setDictTreeDtoValue(children);
            }
        }
    }

    /**
     * findByCodeDictTreeDto
     *
     * @param dictTreeDto 字典树dto
     * @param code code
     * @return com.wiseco.var.process.app.server.service.dto.DictTreeDto
     */
    public DictTreeDto findByCodeDictTreeDto(DictTreeDto dictTreeDto, String code) {
        if (dictTreeDto.getCode().equals(code)) {
            return dictTreeDto;
        }
        if (CollectionUtils.isEmpty(dictTreeDto.getChildren())) {
            return null;
        }
        for (DictTreeDto children : dictTreeDto.getChildren()) {
            DictTreeDto dictTreeDtoParent = findByCodeDictTreeDto(children, code);
            if (null != dictTreeDtoParent) {
                return dictTreeDtoParent;
            }
        }
        return null;
    }

    /**
     * varProcessDictTreeListByNameOrCodeCopy
     *
     * @param varProcessDictList 字典类别列表接口dto
     * @param nameOrCode 名称或者code
     * @param list list
     */
    public void varProcessDictTreeListByNameOrCodeCopy(List<DictTreeDto> varProcessDictList, String nameOrCode, List<DictTreeDto> list) {
        if (CollectionUtils.isEmpty(varProcessDictList) || !CollectionUtils.isEmpty(list)) {
            return;
        }
        for (DictTreeDto childrenDictTreeDto : varProcessDictList) {
            //这里注意contains是包含的意思也就是只要包含nameOrCode这个字符就行，类似于like '%${nameOrCode}%'
            if (childrenDictTreeDto.getName().contains(nameOrCode) || childrenDictTreeDto.getCode().contains(nameOrCode)) {
                list.add(childrenDictTreeDto);
            }
        }
        for (DictTreeDto childrenDictTreeDto : varProcessDictList) {
            varProcessDictTreeListByNameOrCodeCopy(childrenDictTreeDto.getChildren(), nameOrCode, list);
        }
    }

    /**
     * 新增数据变量中字典在字典类型数据表中不存在的数据
     *
     * @param varProcessModelTreeList 前端传入的领域数据模型树形结构列表
     * @param spaceId 变量空间Id
     * @param objectName 对象名称
     */
    public void addDataModelVarEnumNameNotDict(List<DomainModelTree> varProcessModelTreeList, String objectName, Long spaceId) {
        // 数据模型树上定义的字典类型
        List<String> enumNameList = new ArrayList<>();
        for (DomainModelTree varProcessModelTree : varProcessModelTreeList) {
            findDataModelVarEnumName(varProcessModelTree, enumNameList);
            if (CollectionUtils.isEmpty(enumNameList)) {
                // 不存在字典类型: 跳过
                continue;
            }
            // 字典编码去重 (enumName 含义为字典编码)
            List<String> dictCodeList = enumNameList.stream().distinct().collect(Collectors.toList());
            // 从数据库查询指定领域数据模型使用的, 未删除的字典类型
            List<VarProcessDict> storedDomainDictList = varProcessDictService.list(new QueryWrapper<VarProcessDict>().lambda().eq(VarProcessDict::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(VarProcessDict::getVarProcessSpaceId, spaceId).in(VarProcessDict::getCode, dictCodeList).select(VarProcessDict::getCode, VarProcessDict::getName));
            // 构建数据库保存的字典类型编码 Map, key: 字典类型编码, value: 字典类型实例对象
            Map<String, VarProcessDict> storedDomainDictMap = new HashMap<>(MagicNumbers.SIXTEEN);
            if (!CollectionUtils.isEmpty(storedDomainDictList)) {
                storedDomainDictMap = storedDomainDictList.stream().collect(Collectors.toMap(VarProcessDict::getCode, a -> a));
            }
            // (导入数据模型用) 从 Excel 导入的新字典类型 List
            List<VarProcessDict> importedDictList = new ArrayList<>();
            for (String enumName : dictCodeList) {
                if (!storedDomainDictMap.containsKey(enumName)) {
                    // 数据库不存在数据模型中的字典类型编码: 新建编码和中文名称相同的字典类型数据
                    VarProcessDict varProcessDict = new VarProcessDict();
                    varProcessDict.setName(enumName).setCode(enumName).setVarProcessSpaceId(spaceId).setCreatedUser(SessionContext.getSessionUser().getUsername());
                    importedDictList.add(varProcessDict);
                }
            }
            // 将 Excel 导入的字典类型保存至数据库
            if (!CollectionUtils.isEmpty(importedDictList)) {
                varProcessDictService.saveBatch(importedDictList);
            }
        }
    }

    /**
     * 寻找变量中的字典名称
     *
     * @param varProcessModelTree varProcessModelTree
     * @param enumNameList enumNameList
     */
    public void findDataModelVarEnumName(DomainModelTree varProcessModelTree, List<String> enumNameList) {
        if (!StringUtils.isEmpty(varProcessModelTree.getEnumName())) {
            enumNameList.add(varProcessModelTree.getEnumName());
        }
        if (!CollectionUtils.isEmpty(varProcessModelTree.getChildren())) {
            for (DomainModelTree child : varProcessModelTree.getChildren()) {
                findDataModelVarEnumName(child, enumNameList);
            }
        }
    }

}
