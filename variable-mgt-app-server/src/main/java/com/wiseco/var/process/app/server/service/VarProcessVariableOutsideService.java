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
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableOutside;
import com.wiseco.var.process.app.server.service.dto.VariableOutsideServiceDto;

import java.util.List;

/**
 * <p>
 * 变量引用-外部服务及接收对象关系表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableOutsideService extends IService<VarProcessVariableOutside> {

    /**
     * 获取外部服务list
     *
     * @param spaceId 变量空间Id
     * @param variableIds 变量Id的集合
     * @return 外部服务list
     */
    List<VariableOutsideServiceDto> getOutsideServiceList(Long spaceId, List<Long> variableIds);
}
