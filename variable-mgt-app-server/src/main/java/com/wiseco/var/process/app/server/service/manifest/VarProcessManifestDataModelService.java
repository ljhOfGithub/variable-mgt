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

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestForRealTimeServiceVO;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.service.dto.VarProcessDataModelDto;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelManifestUseVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变量发布接口-数据模型映射表 服务类
 * </p>
 *
 * @author liaody
 * @since 2022-08-24
 */
public interface VarProcessManifestDataModelService extends IService<VarProcessManifestDataModel> {

    /**
     * getManifestUseMapping
     *
     * @param spaceId spaceId
     * @param objectName objectName
     * @param objectVersion objectVersion
     * @return VariableDataModelManifestUseVo
     */
    List<VariableDataModelManifestUseVo> getManifestUseMapping(Long spaceId, String objectName, Integer objectVersion);

    /**
     * getManifestForRealTimeService
     *
     * @param spaceId spaceId
     * @param objectName objectName
     * @param objectVersion objectVersion
     * @return ManifestForRealTimeServiceVO
     */
    List<ManifestForRealTimeServiceVO> getManifestForRealTimeService(Long spaceId, String objectName, Integer objectVersion);

    /**
     * getDataModelsAfterSubmit
     *
     * @param manifestId manifestId
     * @return VarProcessDataModel
     */
    List<VarProcessDataModelDto> getDataModelsAfterSubmit(Long manifestId);

    /**
     * 根据清单id拿到数据模型json content
     * @param manifestIds 清单id list
     * @return 清单id，数据模型 content
     */
    List<Map<String, Object>> findDataModelContents(List<Long> manifestIds);

    /**
     * 获取数据模型信息
     * @param manifestIds 清单id list
     * @return list
     */
    List<VarProcessDataModel> getDataModelInfos(List<Long> manifestIds);

    /**
     * 获取手动添加的数据模型（用于变量清单按照最新版本复制时的场景）
     * @param objectNames （自带的数据模型英文名）
     * @param manifestId 变量清单的ID
     * @param spaceId 变量空间的ID
     * @return 手动添加的数据模型
     */
    List<VarProcessDataModel> getDataModelByHandle(List<String> objectNames, Long manifestId, Long spaceId);
}
