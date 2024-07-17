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
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.dto.ManifestVariableDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessManifestVariableDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessVariableDto;
import com.wiseco.var.process.app.server.service.dto.VariableFlowQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishingVariableDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 变量发布接口使用变量表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessManifestVariableService extends IService<VarProcessManifestVariable> {

    /**
     * 获取变量列表
     *
     * @param spaceId     空间id
     * @param variableIds 变量id
     * @return VarProcessVariable
     */
    List<VarProcessVariable> getVariableList(Long spaceId, List<Long> variableIds);

    /**
     * 获取清单Id
     *
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @return VarProcessManifestVariableDto
     */
    List<VarProcessManifestVariableDto> getByManifestId(Long spaceId, Long manifestId);

    /**
     * 获取清单变量列表
     *
     * @param spaceId 空间id
     * @return VarProcessManifestVariable
     */
    List<VarProcessManifestVariable> getManifestVariableList(Long spaceId);

    /**
     * 获取可变流量
     *
     * @param variableFlowQueryDto 流程-变量查询dto
     * @return VarProcessVariable
     */
    List<VarProcessVariable> getVariableFlow(VariableFlowQueryDto variableFlowQueryDto);

    /**
     * 获取变量列表
     *
     * @param variableFlowQueryDto 流程-变量查询dto
     * @return VarProcessVariable
     */
    List<VarProcessVariable> getVariableListInFlow(VariableFlowQueryDto variableFlowQueryDto);

    /**
     * 按标识符获取变量
     *
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @param identifier 唯一标识符
     * @return VarProcessVariable
     */
    VarProcessVariable getVariableByIdentifier(Long spaceId, Long manifestId, String identifier);

    /**
     * 获取缓存清单变量列表
     *
     * @param date 日期
     * @return VarProcessManifestVariableDto
     */
    List<VarProcessManifestVariableDto> getCacheManifestVariableList(String date);

    /**
     * 根据清单Id获取变量列表
     *
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @return VarProcessVariableDto
     */
    List<VarProcessVariableDto> getVariableListByManifestId(Long spaceId, Long manifestId);


    /**
     * 根据清单Id获取使用的变量信息
     *
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @return VarProcessVariableDto
     */
    List<VarProcessVariableDto> getVariableInfosByManifestId(Long spaceId, Long manifestId);

    /**
     * 获取列表
     *
     * @param spaceId 变量空间Id
     * @param variableId 变量Id
     * @return VarProcessManifestVariable
     */
    List<VarProcessManifestVariable> getManifestUseVariableList(Long spaceId, Long variableId);

    /**
     * 查询变量清单版本详情 - 发布变量清单 信息
     *
     * @param spaceId    变量空间 ID
     * @param manifestId 变量清单 ID
     * @return 变量清单版本详情 - 发布变量清单 DTO List
     */
    List<VariableManifestPublishingVariableDTO> getPublishingVariableInfo(Long spaceId, Long manifestId);

    /**
     * 获取清单使用变量数
     *
     * @param manifestIds 清单id list
     * @return 清单使用变量数
     */
    Map<Long,Long> findVariableAmount(List<Long> manifestIds);

    /**
     * 根据清单id查到变量使用信息
     * @param manifestIds 清单id list
     * @return list
     */
    List<ManifestVariableDto> findmanifestVariables(List<Long> manifestIds);

    /**
     * 查询被清单使用的变量id set
     * @param spaceId 空间id
     * @return set
     */
    Set<Long> findUsedVariables(Long spaceId);

    /**
     * 查询变量信息
     *
     * @param manifestId 清单id
     * @param identifier identifier
     * @return VarProcessManifestVariable
     */
    VarProcessVariable getManifestVariableByIdentifier(long manifestId, String identifier);
}
