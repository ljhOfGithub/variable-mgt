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

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestDetailOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelRealTimeServiceUseVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 实时服务-变量清单映射表 服务类
 * </p>
 */

public interface VarProcessServiceManifestService extends IService<VarProcessServiceManifest> {
    /**
     * 获取实时服务引用的映射
     *
     * @param serviceId 实时服务Id
     * @param manifestId 变量清单Id
     * @return 根据变量路径查询变量或公共函数的返回对象
     */
    VariableDataModelRealTimeServiceUseVo getRealTimeServiceUseMapping(Long serviceId, Long manifestId);

    /**
     * 获取服务状态
     *
     * @param id 实时服务Id
     * @return 实时服务对象
     */
    List<VarProcessServiceVersion> getServiceState(Long id);

    /**
     * 根据服务id和清单id list拿到引用关系
     * @param servicdId 服务id
     * @param manifestIds 清单id list
     * @return 服务清单引用关系 VarProcessServiceManifest list
     */
    List<VarProcessServiceManifest> getRefsByServiceIdAndManifestIds(Long servicdId, List<Long> manifestIds);

    /**
     * 获取实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)
     * @param variableIds 传入的变量Id的集合(单指标分析时传一个, 指标对比分析时传多个)
     * @return 实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)
     */
    List<ServiceManifestNameVo> getServiceManifestName(List<Long> variableIds);

    /**
     * 获取单个变量对应的变量清单映射信息
     * @param variableId 变量Id
     * @return 单个变量对应的变量清单映射信息
     */
    List<ServiceManifestNameVo> getVariableAndManifestMapping(Long variableId);

    /**
     * 获取被实时服务使用的清单id set
     * @param spaceId 空间id
     * @return set
     */
    Set<Long> findUsedManifests(Long spaceId);

    /**
     * 根据服务版本id list查到使用的清单name list
     * @param serviceIds 服务版本id list
     * @return map
     */
    Map<Long, List<String>> findManifestNameMap(List<Long> serviceIds);

    /**
     * 获取清单详情
     *
     * @param manifests 变量清单Id的list
     * @return 服务中选择的变量清单的详情Vo
     */
    List<ServiceManifestDetailOutputVo> getManifestDetail(List<Long> manifests);

    /**
     * 获取变量信息
     *
     * @param serviceId 实时服务Id
     * @return 变量信息
     */
    List<VarProcessVariable> findManifestOutputVariableList(Long serviceId);
}
