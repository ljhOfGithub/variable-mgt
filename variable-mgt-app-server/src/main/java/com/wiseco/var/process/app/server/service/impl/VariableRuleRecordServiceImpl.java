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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.controller.vo.VariableProduceRecordVo;
import com.wiseco.var.process.app.server.repository.VarProcessVariableRuleRecordMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableRuleRecord;
import com.wiseco.var.process.app.server.service.VariableRuleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VariableRuleRecordServiceImpl extends ServiceImpl<VarProcessVariableRuleRecordMapper, VarProcessVariableRuleRecord> implements
        VariableRuleRecordService {
    @Autowired
    private VarProcessVariableRuleRecordMapper varProcessVariableRuleRecordMapper;

    @Override
    public IPage<VariableProduceRecordVo> findPageList(Long id, String planName, Page page) {
        return varProcessVariableRuleRecordMapper.findPageList(id, planName, page);

    }

    @Override
    public List<Map<String, Object>> getlistMaps(Long functionId) {
        return varProcessVariableRuleRecordMapper.getListMaps(functionId);
    }

    @Override
    public void removeRuleRecord(Long id) {
        lambdaUpdate().eq(VarProcessVariableRuleRecord::getVariableRuleId, id).remove();
    }

    @Override
    public List<VarProcessVariableRuleRecord> findList(Long functionId) {
        return lambdaQuery().eq(VarProcessVariableRuleRecord::getFunctionId, functionId)
                .eq(VarProcessVariableRuleRecord::getDelFlag, 0).list();
    }

    @Override
    public VariableProduceRecordVo selectByName(String name, String identifier, Long functionId) {
        return varProcessVariableRuleRecordMapper.selectByName(name, identifier, functionId);

    }
}
