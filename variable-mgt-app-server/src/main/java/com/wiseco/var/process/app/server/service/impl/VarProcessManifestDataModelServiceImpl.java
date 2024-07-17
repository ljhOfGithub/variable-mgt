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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestForRealTimeServiceVO;
import com.wiseco.var.process.app.server.repository.VarProcessManifestDataModelMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.service.dto.VarProcessDataModelDto;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelManifestUseVo;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变量清单-数据模型映射表 服务实现类
 * </p>
 *
 * @author liaody
 * @since 2022-08-24
 */
@Service
public class VarProcessManifestDataModelServiceImpl extends ServiceImpl<VarProcessManifestDataModelMapper, VarProcessManifestDataModel> implements
        VarProcessManifestDataModelService {

    @Autowired
    private VarProcessManifestDataModelMapper varProcessManifestDataModelMapper;

    @Override
    public List<VariableDataModelManifestUseVo> getManifestUseMapping(Long spaceId, String objectName, Integer objectVersion) {
        return varProcessManifestDataModelMapper.getManifestUseDataModel(spaceId, objectName, objectVersion);
    }

    @Override
    public List<ManifestForRealTimeServiceVO> getManifestForRealTimeService(Long spaceId, String objectName, Integer objectVersion) {
        return varProcessManifestDataModelMapper.getManifestForRealTimeService(spaceId, objectName, objectVersion);
    }

    @Override
    public List<VarProcessDataModelDto> getDataModelsAfterSubmit(Long manifestId) {
        return varProcessManifestDataModelMapper.getDataModelsAfterSubmit(manifestId);
    }

    @Override
    public List<Map<String, Object>> findDataModelContents(List<Long> manifestIds) {
        if (CollectionUtils.isEmpty(manifestIds)) {
            return new ArrayList<>();
        }
        return varProcessManifestDataModelMapper.findVariableManifestDataModelMappingVos(manifestIds);
    }

    @Override
    public List<VarProcessDataModel> getDataModelInfos(List<Long> manifestIds) {
        return varProcessManifestDataModelMapper.getDataModelInfos(manifestIds);
    }

    /**
     * 获取手动添加的数据模型（用于变量清单按照最新版本复制时的场景）
     * @param objectNames （自带的数据模型英文名）
     * @param manifestId 变量清单的ID
     * @param spaceId 变量空间的ID
     * @return 手动添加的数据模型
     */
    @Override
    public List<VarProcessDataModel> getDataModelByHandle(List<String> objectNames, Long manifestId, Long spaceId) {
        // 1.构造查询条件
        LambdaQueryWrapper<VarProcessManifestDataModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VarProcessManifestDataModel::getManifestId, manifestId);
        wrapper.eq(VarProcessManifestDataModel::getVarProcessSpaceId, spaceId);
        wrapper.notIn(VarProcessManifestDataModel::getObjectName, objectNames);
        wrapper.select(VarProcessManifestDataModel::getObjectName);
        // 2.查出这个变量清单手动添加的数据模型
        List<VarProcessManifestDataModel> varProcessManifestDataModels = varProcessManifestDataModelMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(varProcessManifestDataModels)) {
            return new ArrayList<>();
        }
        // 3.进入数据模型表，查出每一个数据模型的最新版本
        List<VarProcessDataModel> result = new ArrayList<>();
        for (VarProcessManifestDataModel item : varProcessManifestDataModels) {
            VarProcessDataModel maxVersionDataModel = varProcessManifestDataModelMapper.getMaxVersionDataModel(item.getObjectName());
            result.add(maxVersionDataModel);
        }
        return CollectionUtils.isEmpty(result) ? new ArrayList<>() : result;
    }

}
