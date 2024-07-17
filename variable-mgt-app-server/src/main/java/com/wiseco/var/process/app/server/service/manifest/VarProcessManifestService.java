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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.controller.vo.input.VarModelInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestListOutputVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.service.dto.ManifestListQueryDto;
import com.wiseco.var.process.app.server.service.dto.ServiceUsingManifestDto;

import java.util.List;

/**
 * <p>
 * 变量发布接口表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessManifestService extends IService<VarProcessManifest> {

    /**
     * 接口列表
     *
     * @param page 分页查询参数
     * @param queryDto 查询参数
     * @return 实时服务分页列表
     */
    IPage<VarProcessManifest> getManifestList(Page<ManifestListOutputVo> page, ManifestListQueryDto queryDto);

    /**
     * 获取实时服务下最大接口版本号
     *
     * @param serviceId 实时服务 ID
     * @return 实时服务下最大接口版本号
     */
    int getMaximumManifestVersionWithinService(Long serviceId);

    /**
     * 获取服务下启用的变量清单
     *
     * @param serviceId 实时服务 ID
     * @return 变量清单实体类
     */
    VarProcessManifest getEnabledManifestWithinService(Long serviceId);

    /**
     * getUsingService
     *
     * @param manifestId 变量清单Id
     * @param spaceId 空间id
     * @return 实时服务
     */
    List<ServiceUsingManifestDto> findUsingService(Long manifestId,Long spaceId);

    /**
     * 根据服务ID获取它关联的变量清单名称
     *
     * @param serviceId 服务ID
     * @return 服务ID所关联的变量清单名称
     */
    List<String> getManifestNameByServiceId(Long serviceId);

    /**
     * 根据变量id列表查询 使用到的数据模型列表
     *
     * @param varModelInputDto 通过变量清单中的变量id列表查询对应的数据模型dto
     * @return 数据模型列表
     */
    List<VarProcessDataModel> getModelsByVariableIds(VarModelInputDto varModelInputDto);

    /**
     * 获取启用的清单
     *
     * @param spaceId      变量空间Id
     * @param excludedList 已经用过的变量Id的list
     * @param deptCodes     部门code集合
     * @param userNames     用户名称集合
     * @return java.util.List<com.wiseco.var.process.app.server.repository.entity.VarProcessManifest>
     */
    List<VarProcessManifest> getUpManifest(Long spaceId, List<Long> excludedList, List<String> deptCodes, List<String> userNames);

    /**
     * 获取路径
     *
     * @param varIdList 变量Idlist
     * @return 路径
     */
    List<String> getPathes(List<Long> varIdList);

    /**
     * 根据路径获取名称
     *
     * @param pathList 路径list
     * @return 名称
     */
    List<String> getNamesByPathes(List<String> pathList);

    /**
     * 根据名称获取数据模型列表
     *
     * @param spaceId 变量空间Id
     * @param nameList 名称list
     * @return 数据模型列表
     */
    List<VarProcessDataModel> getModelsByNames(Long spaceId, List<String> nameList);

    /**
     * 清单名称模糊匹配获取清单id的list
     *
     * @param callName 调用名称
     * @return 调用id的list
     */
    List<Long> getCallListIds(String callName);

    /**
     * 获取使用某个公共函数的清单list
     *
     * @param funcId 公共函数id
     * @param spaceId 空间id
     * @return 清单list
     */
    List<VarProcessManifest> findManifestUsingFunc(Long funcId, Long spaceId);

}
