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
package com.wiseco.var.process.app.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceListCriteria;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceVersionListInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessServiceVersionInfo;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 变量服务版本表 Mapper 接口
 */
public interface VarProcessServiceVersionMapper extends BaseMapper<VarProcessServiceVersion> {

    /**
     * 获取服务版本信息
     * @param serviceIds 服务id list
     * @return list
     */
    List<VarProcessServiceVersionInfo> findServiceInfos(@Param("serviceIds") List<Long> serviceIds);

    /**
     * 查询服务非停用版本
     * @param inputDto 入参dto
     * @return list
     */
    List<VarProcessServiceVersion> findNonDisabledVersionsByServiceId(@Param("inputDto") ServiceVersionListInputVO inputDto);

    /**
     * 获取被服务流水号使用的数据模型变量
     * @param spaceId 空间id
     * @return list
     */
    @Select("select distinct vpsv.serial_no as varPath from var_process_service_version vpsv \n"
            + "WHERE delete_flag = 1 and vpsv.serial_no IS NOT NULL;")
    List<VariableUseVarPathDto> getVarUseList(@Param("spaceId") Long spaceId);

    /**
     * 根据服务code拿到启用版本的id
     * @param serviceCode 服务code
     * @return 版本id
     */
    @Select("select vpsv.id from var_process_realtime_service vprs join var_process_service_version vpsv on vpsv.service_id = vprs.id \n"
            + "where vpsv.delete_flag = 1 and vpsv.state = 'ENABLED' and service_code  = #{serviceCode}")
    Long findUpVersionIdByCode(@Param("serviceCode") String serviceCode);

    /**
     * 分页查询启用状态实时服务 rpc用
     * @param criteria 查询条件
     * @param objectPage
     * @return page
     */
    Page<RestServiceListOutputVO> findUpServicePage(Page objectPage, @Param("criteria") ServiceListCriteria criteria);

    /**
     * 根据服务版本id拿到服务信息
     * @param serviceIds 服务版本id list
     * @return 服务信息list
     */
    List<ServiceInfoDto> findsServiceListByVersionIds(@Param("serviceIds") List<Long> serviceIds);

    /**
     * findServiceListByState
     *
     * @param states    状态list
     * @param deptCodes 部门code
     * @param userNames 用户名称
     * @return java.util.List<com.wiseco.var.process.app.server.service.dto.ServiceInfoDto>
     */
    List<ServiceInfoDto> findServiceListByState(@Param("states") List<VarProcessServiceStateEnum> states,@Param("deptCodes") List<String> deptCodes,@Param("userNames") List<String> userNames);

    /**
     * 根据服务名查询版本id
     * @param serviceName 服务名称
     * @return 版本id
     */
    @Select("select vpsv.id from var_process_service_version vpsv \n"
            + "join var_process_realtime_service vprs on vprs.id = vpsv.service_id \n"
            + "where vprs.delete_flag = 1 and vpsv.delete_flag = 1 and vprs.service_name = #{serviceName}")
    List<Long> findServiceVersionIdsByName(@Param("serviceName") String serviceName);

    /**
     * 查询所有服务
     * @param deptCodes 部门code
     * @param userNames 用户名称
     * @return list
     */
    List<ServiceInfoDto> findAllServiceInfos(@Param("deptCodes")List<String> deptCodes,@Param("userNames")List<String> userNames);

    /**
     * 根据code查询服务启用版本
     * @param serviceCode 服务code
     * @return ServiceInfoDto
     */
    @Select("select vpsv.id , vprs.service_code as code , vprs.service_name as name , vpsv.state as state  from var_process_realtime_service vprs \n"
            + "join var_process_service_version vpsv on vpsv.service_id = vprs.id \n"
            + "where vprs.service_code = #{serviceCode} and vpsv.delete_flag = 1")
    List<ServiceInfoDto> findUpVersionByCode(@Param("serviceCode") String serviceCode);
}
