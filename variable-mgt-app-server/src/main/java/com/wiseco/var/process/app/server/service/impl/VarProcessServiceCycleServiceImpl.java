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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessServiceCycleMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceCycle;
import com.wiseco.var.process.app.server.service.VarProcessServiceCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 实时服务-生命周期 业务逻辑层接口实现类
 */

@Service
public class VarProcessServiceCycleServiceImpl extends ServiceImpl<VarProcessServiceCycleMapper, VarProcessServiceCycle> implements
        VarProcessServiceCycleService {

    @Autowired
    private VarProcessServiceCycleMapper varProcessServiceCycleMapper;

    /**
     * 根据服务的ID查询出它最新的拒绝原因
     *
     * @param serviceId 服务ID
     * @return 最新的拒绝原因
     */
    @Override
    public VarProcessServiceCycle getLastestVarProcessServiceCycle(Long serviceId) {
        return varProcessServiceCycleMapper.getLastestVarProcessServiceCycle(serviceId);
    }
}
