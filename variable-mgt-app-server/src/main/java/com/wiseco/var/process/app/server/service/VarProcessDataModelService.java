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
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.OutSideParamsOutputVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.service.dto.OutParamsQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelServiceDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 变量空间-数据模型 服务类
 * </p>
 *
 * @author wangxianli
 * @since 2022-08-25
 */
public interface VarProcessDataModelService extends IService<VarProcessDataModel> {
    /**
     * 分页查询
     *
     * @param page 分页对象
     * @param inputDto 输入
     * @return IPage
     */
    IPage<VarProcessDataModel> findPageList(Page page, VariableDataModelQueryInputVO inputDto);

    /**
     *
     *
     * @param spaceId 变量空间 ID
     * @return 最新版本数据模型对象实体类 List
     */
    /**
     * 查询变量空间所有数据模型对象的最新版本
     * @param spaceId 空间ID
     * @param roleDataAuthority 用户的数据权限DTO
     * @return 变量空间中所有数据模型对象的最新版本
     */
    List<VarProcessDataModel> findMaxVersionList(Long spaceId,RoleDataAuthorityDTO roleDataAuthority);

    /**
     * getDataModelMaxVersionList
     *
     * @param sourceType 数据来源
     * @return 数据模型
     */
    List<VarProcessDataModel> getDataModelMaxVersionList(String sourceType);

    /**
     * 根据对象名称查询数据模型
     *
     * @param spaceId 空间Id
     * @param objectNameList 对象名称
     * @return  List
     */
    List<VarProcessDataModel> findListByObjectName(Long spaceId, List<String> objectNameList);

    /**
     * 查询服务列表
     *
     * @param spaceId 空间id
     * @param objectNameList 对象名list
     * @return VariableDataModelServiceDto List
     */
    List<VariableDataModelServiceDto> findServiceListByObjectName(Long spaceId, List<String> objectNameList);

    /**
     * 根据变量清单 ID 查询绑定的数据模型
     *
     * @param manifestId 变量清单 ID
     * @param sourceType 数据来源
     * @return 变量加工数据模型 List
     */
    List<VarProcessDataModel> listDataModelSpecificVersion(Long manifestId, Integer sourceType);

    /**
     * findByDataModelInfo
     *
     * @param objectName 对象名称
     * @param objectVersion 版本
     * @return 数据模型
     */
    VarProcessDataModel findByDataModelInfo(String objectName, Long objectVersion);

    /**
     * findMaxVersionModelsByNames
     *
     * @param nameList 名称List
     * @return 数据模型
     */
    List<VarProcessDataModel> findMaxVersionModelsByNames(List<String> nameList);

    /**
     * findByDataModelInfo
     *
     * @param objectName 对象名称
     * @return 数据模型
     */
    VarProcessDataModel findByDataModelName(String objectName);

    /**
     * findMaxSersionMap
     *
     * @param modelNames 模型名称list
     * @return map key：名称；value：最大版本
     */
    Map<String,Integer> findMaxVersionMap(List<String> modelNames);

    /**
     * 通过模型名称拿到最大版本的conten
     * @param modelNames 模型名list
     * @return content
     */
    Map<String,String> getModelContentsByNames(List<String> modelNames);

    /**
     * 获取外部传入参数
     *
     * @param pageConfig 分页参数
     * @param inputDto 输入实体类对象
     * @return 外部传入参数
     */
    IPage<OutSideParamsOutputVo> findParams(Page<OutSideParamsOutputVo> pageConfig, OutParamsQueryDto inputDto);

    /**
     * 查询所有被使用的数据模型id set
     * @return set
     */
    Set<Long> findAllUsedDatModelIds();
}
