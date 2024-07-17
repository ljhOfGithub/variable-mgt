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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.service.dto.VariableSpaceReferencedOutsideServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量-外部服务引入对象表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Mapper
public interface VarProcessOutsideRefMapper extends BaseMapper<VarProcessOutsideRef> {

    /**
     * 统计引用外部服务数
     *
     * @param spaceId 变量空间 ID
     * @return 引用外部服务数
     */
    @Select("SELECT COUNT(DISTINCT outside_service_id)\n" + "FROM var_process_outside_ref\n" + "WHERE var_process_space_id = #{spaceId}")
    Integer countReferencedOutsideServiceNumber(@Param("spaceId") Long spaceId);

    /**
     * 分页查询变量空间引入的外部服务信息
     *
     * @param pageConfig          分页配置
     * @param spaceId             变量空间 ID
     * @param outsideServiceState 外部服务状态
     * @param referenceState      外部服务引入状态
     * @param keyword             外部服务名称/编码搜索关键词
     * @return 变量空间引入的外部服务信息 分页查询结果
     */
    IPage<VariableSpaceReferencedOutsideServiceInfoDto> getVariableSpaceReferencedOutsideServiceInfoPage(Page<VariableSpaceReferencedOutsideServiceInfoDto> pageConfig,
                                                                                                         @Param("spaceId") Long spaceId,
                                                                                                         @Param("outsideServiceState") List<String> outsideServiceState,
                                                                                                         @Param("referenceState") List<String> referenceState,
                                                                                                         @Param("keyword") String keyword);

    /**
     * 查询变量空间引入的外部服务接收对象信息
     *
     * @param spaceId 变量空间 ID
     * @return 变量空间引入的外部服务接收对象信息 DTO
     */
    List<VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto> getVariableSpaceReferencedOutsideServiceReceiverObjectInfo(@Param("spaceId") Long spaceId);

    /**
     * 查询变量空间引入的外部服务接收对象信息
     *
     * @param dataModelId 数据模型 ID
     * @return 变量空间外部服务引入对象信息 DTO
     */
    VarProcessOutsideRef findByDataModelId(Long dataModelId);
}
