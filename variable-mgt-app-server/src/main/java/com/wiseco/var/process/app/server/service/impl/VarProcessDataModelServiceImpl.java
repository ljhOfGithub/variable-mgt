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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.ObjectUtils;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.OutSideParamsOutputVo;
import com.wiseco.var.process.app.server.repository.VarProcessCompileVarMapper;
import com.wiseco.var.process.app.server.repository.VarProcessDataModelMapper;
import com.wiseco.var.process.app.server.repository.VarProcessFunctionMapper;
import com.wiseco.var.process.app.server.repository.VarProcessManifestDataModelMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.dto.OutParamsQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 变量空间-数据模型 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-08-25
 */
@Service
public class VarProcessDataModelServiceImpl extends ServiceImpl<VarProcessDataModelMapper, VarProcessDataModel> implements VarProcessDataModelService {

    @Autowired
    private VarProcessDataModelMapper varProcessDataModelMapper;

    @Autowired
    private VarProcessCompileVarMapper varProcessCompileVarMapper;

    @Autowired
    private VarProcessManifestDataModelMapper varProcessManifestDataModelMapper;

    @Autowired
    private VarProcessFunctionMapper varProcessFunctionMapper;

    @Override
    public IPage<VarProcessDataModel> findPageList(Page page, VariableDataModelQueryInputVO inputDto) {
        return varProcessDataModelMapper.findPageList(page, inputDto);
    }

    @Override
    public List<VarProcessDataModel> findMaxVersionList(Long spaceId,RoleDataAuthorityDTO roleDataAuthority) {
        if (ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return new ArrayList<>();
        }
        return varProcessDataModelMapper.findMaxVersionList(spaceId,roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
    }

    @Override
    public List<VarProcessDataModel> getDataModelMaxVersionList(String sourceType) {
        return varProcessDataModelMapper.getDataModelMaxVersionList(sourceType);
    }

    @Override
    public List<VarProcessDataModel> findListByObjectName(Long spaceId, List<String> objectNameList) {
        return varProcessDataModelMapper.findListByObjectName(spaceId, objectNameList);
    }

    @Override
    public List<VariableDataModelServiceDto> findServiceListByObjectName(Long spaceId, List<String> objectNameList) {
        return varProcessDataModelMapper.findServiceListByObjectName(spaceId, objectNameList);
    }

    @Override
    public List<VarProcessDataModel> listDataModelSpecificVersion(Long manifestId, Integer sourceType) {
        return varProcessDataModelMapper.listDataModelSpecificVersion(manifestId, sourceType);
    }

    @Override
    public VarProcessDataModel findByDataModelInfo(String objectName, Long objectVersion) {
        return varProcessDataModelMapper.findByDataModelInfo(objectName, objectVersion);
    }

    @Override
    public List<VarProcessDataModel> findMaxVersionModelsByNames(List<String> nameList) {
        return varProcessDataModelMapper.findMaxVersionModelsByNames(nameList);
    }

    @Override
    public VarProcessDataModel findByDataModelName(String objectName) {
        return varProcessDataModelMapper.findByDataModelName(objectName);
    }

    @Override
    public Map<String, Integer> findMaxVersionMap(List<String> modelNames) {
        if (CollectionUtils.isEmpty(modelNames)) {
            return new HashMap<>(MagicNumbers.EIGHT);
        }
        List<VarProcessDataModel> modelList = varProcessDataModelMapper.selectList(Wrappers.<VarProcessDataModel>lambdaQuery()
                .select(VarProcessDataModel::getObjectName, VarProcessDataModel::getVersion)
                .in(VarProcessDataModel::getObjectName, modelNames));

        return modelList.stream()
                .collect(Collectors.groupingBy(VarProcessDataModel::getObjectName,
                        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt(VarProcessDataModel::getVersion)),
                                maxVersionModel -> maxVersionModel.map(VarProcessDataModel::getVersion).orElse(1))));
    }

    @Override
    public Map<String, String> getModelContentsByNames(List<String> modelNames) {
        if (CollectionUtils.isEmpty(modelNames)) {
            return new HashMap<>(MagicNumbers.EIGHT);
        }
        List<VarProcessDataModel> maxModelInfoByNames = varProcessDataModelMapper.getMaxModelInfoByNames(modelNames);
        return maxModelInfoByNames.stream().collect(Collectors.toMap(VarProcessDataModel::getObjectName, VarProcessDataModel::getContent));
    }

    @Override
    public IPage<OutSideParamsOutputVo> findParams(Page<OutSideParamsOutputVo> pageConfig, OutParamsQueryDto inputDto) {
        pageConfig.setCountId("findParamsCount");
        return varProcessDataModelMapper.findParams(pageConfig, inputDto);
    }

    @Override
    public Set<Long> findAllUsedDatModelIds() {
        List<String> usedDataModelNames = varProcessFunctionMapper.findDataModelNameUsedByPrep();
        Set<Long> dataModelUsedByManifest = varProcessManifestDataModelMapper.findAllUsedModelIds();
        if (CollectionUtils.isNotEmpty(usedDataModelNames)) {
            dataModelUsedByManifest.addAll(varProcessDataModelMapper.selectList(Wrappers.<VarProcessDataModel>lambdaQuery().in(VarProcessDataModel::getObjectName,usedDataModelNames))
                    .stream().map(VarProcessDataModel::getId).collect(Collectors.toList()));
        }
        return dataModelUsedByManifest;
    }

}
