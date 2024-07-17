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

import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.enums.DomainModelTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class TemplateModelConverter {

    /**
     * 递归类型转换成DynamicTreeOutputDto且在这个对象中寻找isExtend类型的属性
     * 1，排除基本类型数组
     * 2，对象类型数组，该对象下的属性及子对象的属性都不要
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param loopDataValue loopDataValue
     * @param typeList 筛选类型
     * @return DomainDataModelTreeDto
     */
    public static DomainDataModelTreeDto beanCopyDynamicTreeOutputDtoByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, String loopDataValue, List<String> typeList) {
        if (domainDataModelTreeDto == null || CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return domainDataModelTreeDto;
        }
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
            if (StringUtils.isEmpty(loopDataValue) || !loopDataValue.startsWith(child.getValue())) {
                //对象类型数组，该对象下的属性及子对象的属性都不要
                if (child.getType().equalsIgnoreCase(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                        && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                    child.setChildren(null);
                } else if (child.getType().equalsIgnoreCase(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    //如果是对象，但是不是对象数组，则看对象下是否存在typeList类型的数据，如果存在则继续递归，否则直接返回，不再寻找
                    boolean isTypeList = treeChildrenIsExtendList(child, typeList);
                    //对象中不存在typeList类型的数据，直接返回不再寻找
                    if (!isTypeList) {
                        continue;
                    }
                }
            }

            DomainDataModelTreeDto domainDataModelTreeDtoChild = beanCopyDynamicTreeOutputDtoByTypeList(child, loopDataValue, typeList);
            //等于空不放入到children中，也就是删除
            if (null == domainDataModelTreeDtoChild) {
                continue;
            }
            //当前节点是否是loop节点
            boolean isLoop = false;
            if (!StringUtils.isEmpty(loopDataValue) && loopDataValue.equals(child.getValue())) {
                isLoop = true;
            }
            //类型是否可扩展
            boolean isExtend = false;
            if (!StringUtils.isEmpty(child.getIsExtend()) && "1".equals(child.getIsExtend())) {
                isExtend = true;
            }
            //domainDataModelTreeDtoChild对象中的类型是object
            boolean flag = domainDataModelTreeDtoChild.getType().equalsIgnoreCase(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
            //count>0或者类型是object类型
            if (isExtend || flag || isLoop) {
                //排除基本类型数组
                if (DomainModelTreeUtils.ZERO_VALUE.equals(domainDataModelTreeDtoChild.getIsArr()) || isLoop) {
                    children.add(domainDataModelTreeDtoChild);
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

    /**
     * 判断树形结构对象是否还存在typeList类型
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param typeList 类型list
     * @return boolean
     */
    public static boolean treeChildrenIsExtendList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        if (null == domainDataModelTreeDto.getChildren()) {
            return false;
        }
        for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
            //类型在typeList存在
            if (typeList.contains(child.getType()) && !StringUtils.isEmpty(child.getIsExtend()) && "1".equals(child.getIsExtend())) {
                return true;
            }
            //递归寻找
            if (treeChildrenIsExtendList(child, typeList)) {
                return true;
            }
        }
        return false;
    }

    /**
     * findDomainModelTreeByTypeList
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param loopDataValue loopDataValue
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    public static DomainDataModelTreeDto findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, String loopDataValue) {
        //这里子对象为空，表示是对象没有子对象了
        if (CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return domainDataModelTreeDto;
        }
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
            //当前child是否在loopDataValue下
            boolean isLoop = false;
            if (!StringUtils.isEmpty(loopDataValue) && loopDataValue.startsWith(child.getValue())) {
                isLoop = true;
            }
            //如果是对象数组，则该对象数组和它下面的数据全都不要
            if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                    && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr()) && !isLoop) {
                continue;
            } else if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                //如果是对象，但是不是对象数组(或者在loopvalue路径内)，则看对象下是否存在object类型的数据，如果存在则继续递归，否则直接返回，不再寻找
                boolean isTypeList = treeChildrenIsExtendList(child, Arrays.asList(DataVariableTypeEnum.OBJECT_TYPE.getMessage()));
                //对象中不存在typeList类型的数据，直接返回不再寻找
                if (!isTypeList) {
                    if (!StringUtils.isEmpty(child.getIsExtend()) && "1".equals(child.getIsExtend())) {
                        child.setChildren(null);
                        //这里是为了把没有子对象的对象数据放入到他的父级子对象集合中，因为他自己本身不能丢弃
                        children.add(child);
                        continue;
                    } else {
                        continue;
                    }
                }
                //如果需要继续寻找,子对象中只要对象数据
                getDomainDataModelTreeNotArrayObject(child, loopDataValue);
                DomainDataModelTreeDto domainDataModelTreeDtoChild = findDomainModelTreeByTypeList(child, loopDataValue);
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


    /**
     * getDomainDataModelTreeNotArrayObject
     *
     * @param child 子树
     * @param loopDataValue loopDataValue
     */
    public static void getDomainDataModelTreeNotArrayObject(DomainDataModelTreeDto child, String loopDataValue) {
        if (!CollectionUtils.isEmpty(child.getChildren())) {
            List<DomainDataModelTreeDto> objectChildrenList = new ArrayList<>();
            for (DomainDataModelTreeDto childChild : child.getChildren()) {
                boolean flag = childChild.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                        && (DomainModelTreeUtils.ZERO_VALUE.equals(childChild.getIsArr()) || (!StringUtils.isEmpty(loopDataValue) && loopDataValue
                        .startsWith(childChild.getValue())));
                if (flag) {
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
}
