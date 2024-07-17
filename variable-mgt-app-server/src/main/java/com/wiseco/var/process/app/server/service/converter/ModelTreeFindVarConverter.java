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

import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DomainModelTypeEnum;
import com.wiseco.var.process.app.server.commons.util.ModelTreeUtils;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 树形变量中根据类型寻找变量数据接口
 */
interface DomainModelTreeFindVarTypeInterface {

    /**
     * 根据类型在变量树中寻找想要的数据
     *
     * @param domainDataModelTreeDto
     * @param typeList
     * @return DomainDataModelTreeDto
     */
    default DomainDataModelTreeDto findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        return null;
    }

    /**
     * 根据名称查询变量
     *
     * @param domainDataModelTreeDto
     * @param dataValue
     * @return DomainDataModelTreeDto
     */
    default DomainDataModelTreeDto findDomainModelTreeByName(DomainDataModelTreeDto domainDataModelTreeDto, String dataValue) {
        return null;
    }

    /**
     * 根据类型在变量树中寻找想要的数据
     *
     * @param domainDataModelTreeDto 领域树Dto
     * @param typeList               类型list
     * @param dataValue              数据的值
     * @return 变量树中想要寻找的数据
     */
    default DomainDataModelTreeDto findDomainModelTreeByTypeListAndDataValue(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList, String dataValue) {
        return null;
    }

    /**
     * 根据类型在变量树中寻找想要的数据
     *
     * @param domainDataModelTreeDto DomainDataModelTreeDto
     * @param typeList List
     * @return DomainDataModelTreeDto
     */
    default DomainDataModelTreeDto findDomainModelTreeByType(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        return null;
    }
}

/**
 * 模型树寻找不同变量的转换类
 */
@Component
public class ModelTreeFindVarConverter {

    @Autowired
    private Map<String, DomainModelTreeFindVarTypeInterface> domainModelTreeFindVarTypeInterfaceMap;

    /**
     * 查找非对象数组数组
     *
     * @param child 子树
     */
    public static void getDomainDataModelTreeNotArrayObject(DomainDataModelTreeDto child) {
        if (!CollectionUtils.isEmpty(child.getChildren())) {
            List<DomainDataModelTreeDto> objectChildrenList = new ArrayList<>();
            for (DomainDataModelTreeDto childChild : child.getChildren()) {
                if (childChild.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                        && DomainModelTreeUtils.ZERO_VALUE.equals(childChild.getIsArr())) {
                    objectChildrenList.add(childChild);
                }
            }
            if (!CollectionUtils.isEmpty(objectChildrenList)) {
                child.setChildren(objectChildrenList);
            } else {
                child.setChildren(null);
            }

        }
    }

    /**
     * 根据beanName获取不同的获取变量类型的方法
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param typeList 类型list
     * @param beanName Bean的名称
     * @return DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto getDomainDataModelTreeByListType(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList, String beanName) {
        DomainModelTreeFindVarTypeInterface domainModelTreeFindVarTypeInterface = domainModelTreeFindVarTypeInterfaceMap.get(beanName);
        return domainModelTreeFindVarTypeInterface.findDomainModelTreeByTypeList(domainDataModelTreeDto, typeList);
    }

    /**
     * 根据beanName获取不同的获取变量类型的方法
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param typeList 类型list
     * @param beanName Bean的名称
     * @return DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto getDomainDataModelTreeByType(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList, String beanName) {
        DomainModelTreeFindVarTypeInterface domainModelTreeFindVarTypeInterface = domainModelTreeFindVarTypeInterfaceMap.get(beanName);
        return domainModelTreeFindVarTypeInterface.findDomainModelTreeByType(domainDataModelTreeDto, typeList);
    }

    /**
     * 根据beanName获取不同的获取变量类型的方法
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param typeList 类型list
     * @param beanName bean类的名称
     * @param dataValue 数据的名称
     * @return DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto getDomainDataModelTreeByListTypeAndDataValue(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList, String beanName, String dataValue) {
        DomainModelTreeFindVarTypeInterface domainModelTreeFindVarTypeInterface = domainModelTreeFindVarTypeInterfaceMap.get(beanName);
        return domainModelTreeFindVarTypeInterface.findDomainModelTreeByTypeListAndDataValue(domainDataModelTreeDto, typeList, dataValue);
    }

    /**
     * 根据beanName获取不同的获取变量类型的方法
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param dataValue 数据的值
     * @param beanName bean的名称
     * @return DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto getDomainDataModelTreeByName(DomainDataModelTreeDto domainDataModelTreeDto, String dataValue, String beanName) {
        DomainModelTreeFindVarTypeInterface domainModelTreeFindVarTypeInterface = domainModelTreeFindVarTypeInterfaceMap.get(beanName);
        return domainModelTreeFindVarTypeInterface.findDomainModelTreeByName(domainDataModelTreeDto, dataValue);
    }

}

/**
 * 寻找是array的数据
 */
@Component("domainModelTreeFindVarArrayType")
class DomainModelTreeFindVarArrayType implements DomainModelTreeFindVarTypeInterface {

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        //寻找是array的数据
        ModelTreeUtils.treeChildrenArray(domainDataModelTreeDto);
        return domainDataModelTreeDto;
    }
}

/**
 * 寻找非数组对象的数据
 * 寻找等于类型等于object对象的数据，找到对象为object类型的对象，该对象下的数据全都不要，但是如果该对象下还有对象则需要继续寻找
 */
@Component("domainModelTreeFindVarObjectType")
@Slf4j
class DomainModelTreeFindVarObjectType implements DomainModelTreeFindVarTypeInterface {

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        //这里子对象为空，表示是对象没有子对象了
        if (CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return domainDataModelTreeDto;
        }
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
            //如果是对象数组，则该对象数组和它下面的数据全都不要
            if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                    && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                log.info("这是一个空if…");
            } else if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                //如果是对象，但是不是对象数组，则看对象下是否存在object类型的数据，如果存在则继续递归，否则直接返回，不再寻找
                boolean isTypeList = DomainModelTreeEntityUtils.treeChildrenIsTypeList(child, typeList);
                //对象中不存在typeList类型的数据，直接返回不再寻找
                if (!isTypeList) {
                    child.setChildren(null);
                    //这里是为了把没有子对象的对象数据放入到他的父级子对象集合中，因为他自己本身不能丢弃
                    children.add(child);
                    continue;
                }
                //如果需要继续寻找,子对象中只要对象数据
                ModelTreeFindVarConverter.getDomainDataModelTreeNotArrayObject(child);
                DomainDataModelTreeDto domainDataModelTreeDtoChild = findDomainModelTreeByTypeList(child, typeList);
                //等于空不放入到children中，也就是删除
                if (null != domainDataModelTreeDtoChild) {
                    children.add(domainDataModelTreeDtoChild);
                }
            }
        }
        if (!CollectionUtils.isEmpty(children)) {
            domainDataModelTreeDto.setChildren(children);
        } else {
            return null;
        }

        return domainDataModelTreeDto;
    }

}

/**
 * 寻找基础数据类型的数据，对象数据和非对象数组都不要
 */
@Component("domainModelTreeFindVarBaseType")
class DomainModelTreeFindVarBaseType implements DomainModelTreeFindVarTypeInterface {

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        domainDataModelTreeDto = DomainModelTreeEntityUtils.beanCopyDynamicTreeOutputDtoByTypeList(domainDataModelTreeDto, typeList);
        //如果根节点的children等于null则表示树中没有匹配的typeList类型
        if (domainDataModelTreeDto == null || CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return null;
        }
        return domainDataModelTreeDto;
    }

}

/**
 * 寻找基础数据类型的数组，只要基础类型数组其它全不要
 */
@Component("domainModelTreeFindVarBaseArrayType")
class DomainModelTreeFindVarBaseArrayType implements DomainModelTreeFindVarTypeInterface {

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        //子对象为空返回空
        if (domainDataModelTreeDto == null || CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return null;
        }
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
            //如果是对象数组，则改对象数组和它下面的数据全都不要
            if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                    && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                if (typeList.contains(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    child.setChildren(null);
                    children.add(child);
                }
            } else if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                //如果是对象，则判断对象下是否还存在typeList基本类型的数组，如果存在则需要继续寻找，否则不再往下寻找
                boolean isTypeList = DomainModelTreeEntityUtils.treeChildrenIsBaseArray(child, typeList);
                //如果对象下存在typeList中类型的数组，则需要递归寻找
                if (isTypeList) {
                    DomainDataModelTreeDto domainDataModelTreeDtoChild = findDomainModelTreeByTypeList(child, typeList);
                    //等于空不放入到children中，也就是删除
                    if (null != domainDataModelTreeDtoChild) {
                        children.add(domainDataModelTreeDtoChild);
                    }
                }
            } else if (!child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                    && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr()) && typeList.contains(child.getType())) {
                //如果是基础类型数组，且类型在typeList中
                children.add(child);
            }
        }

        //children不为空才放入到domainDataModelTreeDto中，否则返回空，也就是删除
        if (!CollectionUtils.isEmpty(children)) {
            domainDataModelTreeDto.setChildren(children);
        } else {
            return null;
        }

        return domainDataModelTreeDto;
    }

}

/**
 * 寻找对象数组+属性，只要基础类型数组其它全不要，并且树的深度和类型要完全匹配
 */
@Component("domainModelTreeFindVarObjectArrayAndPropertyType")
class DomainModelTreeFindVarObjectArrayAndPropertyType implements DomainModelTreeFindVarTypeInterface {

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        //子对象为空返回空
        if (domainDataModelTreeDto == null || CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return null;
        }
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
            final String[] split = typeList.get(0).split("-");
            if (child.getType().equalsIgnoreCase(split[0]) && child.getIsArr().equals(split[1])) {
                if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    if (typeList.size() > 1) {
                        DomainDataModelTreeDto dtoChild = findDomainModelTreeByTypeList(child, typeList.subList(1, typeList.size()));
                        if (dtoChild != null) {
                            children.add(dtoChild);
                        }
                    } else {
                        child.setChildren(null);
                        children.add(child);
                    }
                } else {
                    children.add(child);
                }
            }
        }
        //children不为空才放入到domainDataModelTreeDto中
        if (!CollectionUtils.isEmpty(children)) {
            domainDataModelTreeDto.setChildren(children);
        } else {
            return null;
        }

        return domainDataModelTreeDto;
    }

}

/**
 * 对象动态获取
 */
@Component("domainModelTreeFindVarObjectDynamicType")
class DomainModelTreeFindVarObjectDynamicType implements DomainModelTreeFindVarTypeInterface {

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByName(DomainDataModelTreeDto domainDataModelTreeDto, String dataValue) {
        //和排除的路径相等直接返回null
        if (domainDataModelTreeDto == null || dataValue.equals(domainDataModelTreeDto.getValue())) {
            return null;
        }
        //子对象为空返回空
        if (CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return null;
        }
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
            //如果与过滤的路径不相等且不是对象数组则需要继续匹配，否则丢弃
            if (!dataValue.equals(child.getValue())
                    && child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                    && DomainModelTreeUtils.ZERO_VALUE.equals(child.getIsArr())) {
                //如果是对象，则判断子集是否还存在非数组对象
                boolean isTypeList = DomainModelTreeEntityUtils.treeChildrenByIsArr(child, DomainModelTreeUtils.ZERO_VALUE);
                //如果子集中不存在非对象数组了且路径等于非排除的路径，则不需要继续递归了
                if (!isTypeList && !dataValue.equals(child.getValue())) {
                    //丢弃掉子集
                    child.setChildren(null);
                    children.add(child);
                } else {
                    //如果需要继续寻找,子对象中只要非数组对象数据
                    ModelTreeFindVarConverter.getDomainDataModelTreeNotArrayObject(child);
                    DomainDataModelTreeDto domainDataModelTreeDtoChild = findDomainModelTreeByName(child, dataValue);
                    //等于空不放入到children中，也就是删除
                    if (null != domainDataModelTreeDtoChild) {
                        children.add(domainDataModelTreeDtoChild);
                    } else {
                        children.add(child);
                    }
                }
            }
        }

        //children不为空才放入到domainDataModelTreeDto中，否则返回空，也就是删除
        if (!CollectionUtils.isEmpty(children)) {
            domainDataModelTreeDto.setChildren(children);
        } else {
            domainDataModelTreeDto.setChildren(null);
            return null;
        }

        return domainDataModelTreeDto;
    }

}

/**
 * 对象数组动态获取
 */
@Component("domainModelTreeFindVarObjectArrayDynamicType")
class DomainModelTreeFindVarObjectArrayDynamicType implements DomainModelTreeFindVarTypeInterface {

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByName(DomainDataModelTreeDto domainDataModelTreeDto, String dataValue) {
        //和排除的路径相等直接返回null
        if (domainDataModelTreeDto == null || dataValue.equals(domainDataModelTreeDto.getValue())) {
            return domainDataModelTreeDto;
        }
        //子对象为空返回空
        if (CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return null;
        }
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
            //如果与路径不相等且不是对象数组则需要继续匹配，否则丢弃
            if (!dataValue.equals(domainDataModelTreeDto.getValue())
                    && child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                    && DomainModelTreeUtils.ZERO_VALUE.equals(child.getIsArr())) {
                //如果是对象，则判断子集是否还存在数组对象
                boolean isTypeList = DomainModelTreeEntityUtils.treeChildrenByIsArr(child, DomainModelTreeUtils.ONE_VALUE);
                if (isTypeList) {
                    DomainDataModelTreeDto domainDataModelTreeDtoChild = findDomainModelTreeByName(child, dataValue);
                    //等于空不放入到children中，也就是删除
                    if (null != domainDataModelTreeDtoChild) {
                        //丢弃子集
                        children.add(domainDataModelTreeDtoChild);
                    }
                }
            } else if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                    && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                //如果是对象数组且不等于要排除的路径直接加入进去，丢弃子集
                if (!dataValue.equals(child.getValue())) {
                    child.setChildren(null);
                    children.add(child);
                }
            }
        }

        //children不为空才放入到domainDataModelTreeDto中，否则返回空，也就是删除
        if (!CollectionUtils.isEmpty(children)) {
            domainDataModelTreeDto.setChildren(children);
        } else {
            return null;
        }

        return domainDataModelTreeDto;
    }

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        //寻找是array的数据
        DomainModelTreeEntityUtils.treeChildrenArrayObject(domainDataModelTreeDto);
        return domainDataModelTreeDto;

    }

    @Override
    public DomainDataModelTreeDto findDomainModelTreeByType(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        //寻找是array的数据
        DomainModelTreeEntityUtils.treeChildrenArrayObject(domainDataModelTreeDto);
        return domainDataModelTreeDto;

    }

    /**
     * 寻找基础数据类型和数据类型的数组
     */
    @Component("domainModelTreeFindVarBaseTypeAndBaseArrayType")
    static class DomainModelTreeFindVarBaseTypeAndBaseArrayType implements DomainModelTreeFindVarTypeInterface {

        @Override
        public DomainDataModelTreeDto findDomainModelTreeByTypeListAndDataValue(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList, String dataValue) {
            //子对象为空返回空
            if (CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
                return null;
            }
            List<DomainDataModelTreeDto> children = new ArrayList<>();
            for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
                if (!StringUtils.isEmpty(dataValue)) {
                    //如果是对象数组，则改对象数组和它下面的数据全都不要
                    if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                            && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr()) && dataValue.equals(child.getValue())) {
                        DomainDataModelTreeDto domainDataModelTreeDtoChild = findDomainModelTreeByTypeListAndDataValue(child, typeList, "");
                        if (null != domainDataModelTreeDtoChild) {
                            return domainDataModelTreeDtoChild;
                        }

                    }
                    //判断是否和路径相等
                    if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                        List<String> objectList = new ArrayList<>();
                        objectList.add("object");
                        boolean isTypeList = DomainModelTreeEntityUtils.treeChildrenIsBaseArray(child, objectList);
                        //如果对象下存在typeList中类型的数组，则需要递归寻找
                        if (isTypeList) {
                            DomainDataModelTreeDto domainDataModelTreeDtoChild = findDomainModelTreeByTypeListAndDataValue(child, typeList, dataValue);
                            if (null != domainDataModelTreeDtoChild) {
                                return domainDataModelTreeDtoChild;
                            }
                        }
                        if (typeList.contains(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                            children.add(child);
                        }
                    }

                } else {
                    //如果是对象数组，则改对象数组和它下面的数据全都不要
                    if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                            && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                        if (typeList.contains(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                            children.add(child);
                        }
                    } else if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage()) && !DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                        DomainDataModelTreeDto domainDataModelTreeDtoChild = findDomainModelTreeByTypeListAndDataValue(child, typeList, "");
                        //等于空不放入到children中，也就是删除
                        if (null != domainDataModelTreeDtoChild) {
                            children.add(domainDataModelTreeDtoChild);
                        }

                    } else if (!child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                            && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr()) && typeList.contains(child.getType())) {
                        //如果是基础类型数组，且类型在typeList中
                        children.add(child);
                    } else if (!child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage()) && typeList.contains(child.getType())) {
                        children.add(child);
                    }
                }

            }

            //children不为空才放入到domainDataModelTreeDto中，否则返回空，也就是删除
            if (!CollectionUtils.isEmpty(children)) {
                domainDataModelTreeDto.setChildren(children);
            } else {
                return null;
            }

            return domainDataModelTreeDto;
        }

    }
}
