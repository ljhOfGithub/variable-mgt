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
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.service.dto.VariableSpaceReferencedOutsideServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto;

import java.util.List;

/**
 * <p>
 * 变量-外部服务引入对象表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessOutsideRefService extends IService<VarProcessOutsideRef> {

    /**
     * 统计引用外部服务数
     *
     * @param spaceId 变量空间 ID
     * @return 引用外部服务数
     */
    Integer countReferencedOutsideServiceNumber(Long spaceId);

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
                                                                                                         Long spaceId,
                                                                                                         List<String> outsideServiceState,
                                                                                                         List<String> referenceState, String keyword);

    /**
     * 查询变量空间引入的外部服务接收对象信息
     *
     * @param spaceId 变量空间 ID
     * @return 变量空间引入的外部服务接收对象信息 DTO
     */
    List<VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto> getVariableSpaceReferencedOutsideServiceReceiverObjectInfo(Long spaceId);

    /**
     * 通过数据模型Id寻找外部引用
     *
     * @param id 数据模型Id
     * @return 外部引用对象
     */
    VarProcessOutsideRef findByDataModelId(Long id);
}
