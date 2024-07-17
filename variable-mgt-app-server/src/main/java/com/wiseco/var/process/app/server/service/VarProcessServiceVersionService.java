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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceListCriteria;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceVersionListInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.enums.VarProcessServiceActionEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessServiceVersionInfo;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;

import java.util.List;

/**
 * 变量服务版本表 服务类
 */
public interface VarProcessServiceVersionService extends IService<VarProcessServiceVersion> {

    /**
     * 查询服务版本总数等信息
     * @param serviceIds 服务id
     * @return list
     */
    List<VarProcessServiceVersionInfo> findServiceInfos(List<Long> serviceIds);

    /**
     * 查询非停用的版本
     * @param inputDto 入参dto
     * @return list
     */
    List<VarProcessServiceVersion> findNonDisabledVersionsByServiceId(ServiceVersionListInputVO inputDto);

    /**
     * 更新服务版本状态
     * @param versionEntity 版本对象
     * @param actionType 操作
     * @param desContent 提交、审核通过、审核拒绝的内容
     */
    void updateState(VarProcessServiceVersion versionEntity, VarProcessServiceActionEnum actionType, String desContent);

    /**
     * 获取被服务流水号使用的数据模型变量
     * @param spaceId 空间id
     * @return list
     */
    List<VariableUseVarPathDto> getVarUseList(Long spaceId);

    /**
     * 根据服务code拿到启用版本的id rpc用
     * @param serviceCode 服务code
     * @return 版本id
     */
    Long findUpVersionIdByCode(String serviceCode);

    /**
     * 分页查询启用状态实时服务 rpc用
     * @param criteria 查询条件
     * @return page
     */
    Page<RestServiceListOutputVO> findUpServicePage(ServiceListCriteria criteria);

    /**
     * 根据名称和版本查询服务某一版本
     * @param serviceName 服务名
     * @param serviceVersion 版本号
     * @return VarProcessServiceVersion
     */
    VarProcessServiceVersion findServiceByNameAndVersion(String serviceName, Integer serviceVersion);

    /**
     * 按状态查找服务版本Id列表
     * @param list 状态list
     * @return list
     */
    List<Long> findServiceIdListByState(List<VarProcessServiceStateEnum> list);

    /**
     * 根据服务版本id拿到服务信息
     * @param serviceIds 服务版本id list
     * @return 服务信息list
     */
    List<ServiceInfoDto> findserviceListByVersionIds(List<Long> serviceIds);

    /**
     * 根据状态查询服务信息
     * @param states 状态list
     * @param deptCodes 部门code
     * @param userNames 用户名称
     * @return list
     */
    List<ServiceInfoDto> findServiceListByState(List<VarProcessServiceStateEnum> states, List<String> deptCodes, List<String> userNames);

    /**
     * 根据服务名查询版本id
     * @param serviceName 服务名称
     * @return 版本id
     */
    List<Long> findServiceVersionIdsByName(String serviceName);

    /**
     * 查询所有服务
     *
     * @param deptCodes 部门code
     * @param userNames 用户名称
     * @return list
     */
    List<ServiceInfoDto> findAllServiceInfos(List<String> deptCodes,List<String> userNames);

    /**
     * 通过实时服务(与code相关的)的ID，获取这一组的最高版本号
     * @param serviceId 实时服务(与code相关的)ID
     * @return 最高版本号
     */
    Integer getMaxVersionByServiceId(Long serviceId);

    /**
     * 根据code查询服务启用版本
     * @param serviceCode 服务code
     * @return ServiceInfoDto
     */
    List<ServiceInfoDto> findUpVersionByCode(String serviceCode);
}
