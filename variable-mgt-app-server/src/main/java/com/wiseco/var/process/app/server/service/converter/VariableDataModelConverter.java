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
import com.decision.jsonschema.util.enums.DomainModelSheetNameEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class VariableDataModelConverter {
    /**
     * dataModelObjectToTree
     * @param dataModelList 数据模型list
     * @param excludeObjectName 排除的对象名称
     * @return JSONObject
     */
    public JSONObject dataModelObjectToTree(List<VarProcessDataModel> dataModelList, String excludeObjectName) {
        //排序
        Collections.sort(dataModelList, new Comparator<VarProcessDataModel>() {

            //重写compare方法
            @Override
            public int compare(VarProcessDataModel a, VarProcessDataModel b) {

                String valA = a.getObjectName().toLowerCase();
                String valB = b.getObjectName().toLowerCase();

                return valA.compareTo(valB);
            }
        });

        DomainModelTree rootModelTree = new DomainModelTree();
        rootModelTree.setName(DomainModelSheetNameEnum.RAW_DATA.getMessage());
        rootModelTree.setDescribe(DomainModelSheetNameEnum.RAW_DATA.getDescribe());
        rootModelTree.setValue(DomainModelSheetNameEnum.RAW_DATA.getMessage());
        rootModelTree.setLabel(DomainModelSheetNameEnum.RAW_DATA.getMessage() + "-" + DomainModelSheetNameEnum.RAW_DATA.getDescribe());
        rootModelTree.setType(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        List<DomainModelTree> children = new ArrayList<>();
        for (VarProcessDataModel varProcessDataModel : dataModelList) {
            if (StringUtils.isEmpty(varProcessDataModel.getContent())) {
                continue;
            }
            if (!StringUtils.isEmpty(excludeObjectName) && varProcessDataModel.getObjectName().equals(excludeObjectName)) {
                continue;
            }
            JSONObject jsonObject = JSON.parseObject(varProcessDataModel.getContent(), Feature.OrderedField);
            DomainModelTree domainModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(jsonObject);

            children.add(domainModelTree);
        }
        rootModelTree.setChildren(children);

        return DomainModelTreeUtils.domainModelTreeConvertJsonObject(rootModelTree);
    }
}
