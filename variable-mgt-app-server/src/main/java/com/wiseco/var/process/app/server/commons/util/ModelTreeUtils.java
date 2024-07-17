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
package com.wiseco.var.process.app.server.commons.util;

import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DomainModelTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型树工具类
 */
public class ModelTreeUtils {

    /**
     * 判断树形结构中的children中的对象
     * 
     * @param domainDataModelTreeDto
     * @return
     */
    public static void treeChildrenArray(DomainDataModelTreeDto domainDataModelTreeDto) {
        if (null != domainDataModelTreeDto.getChildren()) {
            List<DomainDataModelTreeDto> childrenList = new ArrayList<>();
            for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
                //是对象数组
                if (DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())
                        && child.getType().equalsIgnoreCase(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    //对象数组直接把children置空且不再往下寻找
                    child.setChildren(null);
                    childrenList.add(child);
                } else if (child.getType().equalsIgnoreCase(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    //为对象，但是不是数组，递归继续寻找对象下的数组
                    if (DomainModelTreeEntityUtils.treeChildrenIsArray(child)) {
                        treeChildrenArray(child);
                        childrenList.add(child);
                    }
                } else if (DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                    //属性为数组
                    childrenList.add(child);
                }
            }
            if (!org.apache.commons.collections.CollectionUtils.isEmpty(childrenList)) {
                domainDataModelTreeDto.setChildren(childrenList);
            } else {
                domainDataModelTreeDto.setChildren(new ArrayList<>());
            }
        }
    }
}
