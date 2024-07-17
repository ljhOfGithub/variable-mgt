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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDeployContentOverviewDto;
import com.wiseco.var.process.app.server.service.dto.VariableMaximumListedVersionQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableQueryDto;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 变量表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableService extends IService<VarProcessVariable> {

    /**
     * 查询最大版本变量list
     *
     * @param page page
     * @param queryDto queryDto
     * @return 分页查询结果
     */
    IPage<VariableDetailDto> findVariableMaxVersionList(Page page, VariableQueryDto queryDto);

    /**
     * 分页查询变量已上架的最大版本
     *
     * @param queryDto 所有变量最大已上架版本记录查询 DTO
     * @return 变量空间表实体类 分页封装
     */
    List<VarProcessVariable> pageQueryVariableMaximumListedVersion(VariableMaximumListedVersionQueryDto queryDto);

    /**
     * 获取变量list
     *
     * @param queryDto queryDto
     * @return 变量list
     */
    List<VarProcessVariable> getList(VariableQueryDto queryDto);

    /**
     * 根据identifier获取变量最大版本
     *
     * @param spaceId spaceId
     * @param identifier identifier
     * @return 最大版本号(Integer)
     */
    Integer getMaxVersion(Long spaceId, String identifier);

    /**
     * 查询变量清单输出变量 List
     *
     * @param manifestId 变量清单 ID
     * @return 变量信息 List
     */
    List<VarProcessVariable> findManifestOutputVariableList(Long manifestId);

    /**
     * 分页查询变量清单发布数据预览信息
     *
     * @param pageConfig 分页配置
     * @param manifestId 变量清单 ID
     * @return 变量清单发布数据预览 DTO 分页封装
     */
    IPage<VariableManifestDeployContentOverviewDto> getManifestDeployContentPage(Page<VariableManifestDeployContentOverviewDto> pageConfig,
                                                                                 Long manifestId);

    /**
     * 根据原变量的ID，获取它下面所有最新版本的发布变量ID
     *
     * @param archetypeManifestId 原变量的ID
     * @return 所有最新版本的发布变量ID
     */
    List<Long> getNewVersionOfVariables(Long archetypeManifestId);

    /**
     * 获取某变量所引用的其它变量对象列表
     *
     * @param variableId 变量Id
     * @return 某变量所引用的其它变量对象列表
     */
    List<VarProcessVariable> getVariablesByVariableId(Long variableId);

    /**
     * 通过变量名称跟变量模板id，是否存在
     *
     * @param functionId functionId
     * @param name name
     * @param identifier identifier
     * @return VarProcessVariable
     */
    VarProcessVariable checkRuleVariable(Long functionId, String name, String identifier);

    /**
     * 条件+分页查询变量
     * @param variableIds 可能的目标变量
     * @param categoryId 分类Id
     * @param dataType 数据类型
     * @param users 可能的创建人
     * @param keyword 关键词(用于变量名称/编码的模糊查询)
     * @param order 排序
     * @return 变量列表
     */
    List<VarProcessVariable> getVariableList(List<Long> variableIds, Long categoryId, String dataType, List<String> users, String keyword, String order);

    /**
     * 获取所有启用的实时服务下, 所关联的启用变量清单, 然后根据这些变量清单，获取所有启用的变量Id
     * @return 变量列表
     */
    Set<Long> variableIdsByOther();

    /**
     * 查询与表中重复的名称
     * @param names
     * @return name list
     */
    List<String> checkNameRepeat(List<String> names);

    /**
     * 查询与表中重复的code
     * @param codes
     * @return code list
     */
    List<String> checkCodeRepeat(List<String> codes);
}
