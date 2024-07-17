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
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;

import java.util.List;

/**
 * <p>
 * 变量-引用数据模型变量关系表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableVarService extends IService<VarProcessVariableVar> {

    /**
     * 获取变量使用情况
     *
     * @param spaceId spaceId
     * @return list
     */
    List<VariableUseVarPathDto> getVarUseList(Long spaceId);

    /**
     * 获取变量使用情况
     * @param queryList queryList
     * @param varFullPath varFullPath
     * @return list
     */
    List<VariableUseVarPathDto> getVarUse(List<VariableUseVarPathDto> queryList, String varFullPath);
}
