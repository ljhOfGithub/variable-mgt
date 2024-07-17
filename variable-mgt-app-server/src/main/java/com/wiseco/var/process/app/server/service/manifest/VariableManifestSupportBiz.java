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
package com.wiseco.var.process.app.server.service.manifest;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.VarProcessVariableVarService;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDto;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 变量清单的变量相关支持逻辑 Bean
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/1
 */
@Component
public class VariableManifestSupportBiz {

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;

    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;

    @Autowired
    private VarProcessVariableVarService varProcessVariableVarService;

    @Autowired
    private VarProcessDataModelService varProcessDataModelService;

    /**
     * 获取变量清单 DTO
     * <p>包含业务信息</p>
     *
     * @param manifestId 变量清单 ID
     * @return 变量清单 DTO
     */
    public VariableManifestDto getVariableManifestDto(Long manifestId) {
        // 查询变量清单所有业务相关信息并返回 DTO
        VarProcessManifest manifestEntity = varProcessManifestService.getById(manifestId);
        if (manifestEntity == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "未查询到变量清单相关信息");
        }
        List<VarProcessManifestVariable> manifestVariableEntityList = varProcessManifestVariableService.list(Wrappers.<VarProcessManifestVariable>lambdaQuery()
                .eq(VarProcessManifestVariable::getManifestId, manifestId));

        List<VarProcessManifestDataModel> manifestMappingEntityList = varProcessManifestDataModelService.list(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                .eq(VarProcessManifestDataModel::getManifestId, manifestId));
        if (VarProcessManifestStateEnum.EDIT.equals(manifestEntity.getState())) {
            //编辑中 -> 将数据模型版本更新为最新
            List<String> modelNames = manifestMappingEntityList.stream().map(VarProcessManifestDataModel::getObjectName).collect(Collectors.toList());
            Map<String, Integer> maxVersionMap = varProcessDataModelService.findMaxVersionMap(modelNames);
            manifestMappingEntityList.forEach(item -> item.setObjectVersion(maxVersionMap.get(item.getObjectName())));
        }

        return VariableManifestDto.builder()
                .manifestEntity(manifestEntity)
                .variablePublishList(manifestVariableEntityList)
                .dataModelMappingList(manifestMappingEntityList)
                .build();
    }

    /**
     * 获取被待发布变量使用的扩展数据
     *
     * @param manifestDto 变量清单 DTO
     * @return 待发布变量使用的扩展数据变量路径 Map
     * <p>key: 待发布变量 ID, value: 扩展数据变量路径 Set</p>
     */
    public Set<String> getExtendedPropertiesUtilizedByPublishingVariable(VariableManifestDto manifestDto) {
        // 0. 查询待发布变量使用的属性
        List<Long> publishingVariableIdList = manifestDto.getVariablePublishList().stream()
                .map(VarProcessManifestVariable::getVariableId)
                .collect(Collectors.toList());
        List<VarProcessVariableVar> variableUtilizedPropertyList = varProcessVariableVarService.list(
                Wrappers.<VarProcessVariableVar>lambdaQuery()
                        .in(VarProcessVariableVar::getVariableId, publishingVariableIdList)
        );

        Set<String> varPathMap = new HashSet<>();
        for (VarProcessVariableVar variableVar : variableUtilizedPropertyList) {
            if (variableVar.getIsExtend() == null || variableVar.getIsExtend() == 0) {
                continue;
            }

            String[] varPathArr = variableVar.getVarPath().split("\\.");
            if (varPathArr[0].equals(PositionVarEnum.RAW_DATA.getName()) && varPathArr.length > 1) {
                varPathMap.add(varPathArr[1]);
            }
        }

        return varPathMap;
    }

    /**
     * 从数据模型树形结构, 提取扩展数据变量路径
     *
     * @param treeDto                      数据模型树形结构
     * @param extendedPropertiesVarPathSet 扩展数据变量路径
     */
    private void extractExtendedPropertiesVarPath(DomainDataModelTreeDto treeDto, Set<String> extendedPropertiesVarPathSet) {
        // 边界条件: 若传入 treeDto 为空, 则立刻返回
        if (null == treeDto) {
            return;
        }
        if (NumberUtils.INTEGER_ONE.toString().equals(treeDto.getIsExtend())) {
            extendedPropertiesVarPathSet.add(treeDto.getValue());
        }
        if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(treeDto.getType()) && !CollectionUtils.isEmpty(treeDto.getChildren())) {
            for (DomainDataModelTreeDto child : treeDto.getChildren()) {
                extractExtendedPropertiesVarPath(child, extendedPropertiesVarPathSet);
            }
        }
    }
}
