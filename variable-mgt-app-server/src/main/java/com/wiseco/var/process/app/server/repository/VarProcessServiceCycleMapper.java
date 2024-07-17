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
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceCycle;
import org.apache.ibatis.annotations.Param;

/**
 * 实时服务-生命周期 的 Mapper接口
 */

public interface VarProcessServiceCycleMapper extends BaseMapper<VarProcessServiceCycle> {

    /**
     * 根据服务的ID查询出它最新的拒绝原因
     *
     * @param serviceId 服务ID
     * @return 最新的拒绝原因
     */
    VarProcessServiceCycle getLastestVarProcessServiceCycle(@Param("serviceId") Long serviceId);
}
