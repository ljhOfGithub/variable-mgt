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
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVar;
import com.wiseco.var.process.app.server.service.dto.ManifestVarForRealTimeServiceVarPathDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;

import java.util.List;

/**
 * <p>
 * 变量清单-引用数据模型变量关系表 服务类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-14
 */
public interface VarProcessManifestVarService extends IService<VarProcessManifestVar> {
    /**
     * 获取使用的模型列表
     *
     * @param spaceId 变量空间Id
     * @return 使用的模型列表
     */
    List<VariableUseVarPathDto> getVarUseList(Long spaceId);

    /**
     * 变量清单使用的模型列表
     *
     * @param spaceId 变量空间Id
     * @return 变量清单使用的模型列表
     */
    List<VariableUseVarPathDto> getManifestVarUseList(Long spaceId);

    /**
     * getManifestVarForRealTimeService
     *
     * @param spaceId 变量空间Id
     * @return ManifestVarForRealTimeServiceVarPathDto
     */
    List<ManifestVarForRealTimeServiceVarPathDto> getManifestVarForRealTimeService(Long spaceId);

    /**
     * 查询所有清单流程中直接使用的数据模型变量
     * @param spaceId 空间id
     * @return list
     */
    List<VariableUseVarPathDto> getSelfVarUseList(Long spaceId);
}
