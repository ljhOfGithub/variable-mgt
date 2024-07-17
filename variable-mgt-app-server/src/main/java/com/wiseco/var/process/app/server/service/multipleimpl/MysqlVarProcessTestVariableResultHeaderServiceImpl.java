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
package com.wiseco.var.process.app.server.service.multipleimpl;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.VarProcessTestVariableResultHeaderMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableResultHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@MessageCondition.OnMysqlMessageEnabled
public class MysqlVarProcessTestVariableResultHeaderServiceImpl implements VarProcessTestVariableResultHeaderService {

    @Autowired
    private VarProcessTestVariableResultHeaderMapper    varProcessTestVariableResultHeaderMapper;
    @Override
    public void saveOrUpdateHeader(Long resultId, String newResultHeader) {
        VarProcessTestVariableResultHeader oldTestVariableResultHeader = varProcessTestVariableResultHeaderMapper.selectOne(Wrappers.<VarProcessTestVariableResultHeader>lambdaQuery()
                .eq(VarProcessTestVariableResultHeader::getResultId, resultId));
        if (oldTestVariableResultHeader != null) {
            oldTestVariableResultHeader.setResultHeader(newResultHeader);
            varProcessTestVariableResultHeaderMapper.updateById(oldTestVariableResultHeader);
        } else {
            varProcessTestVariableResultHeaderMapper.insert(VarProcessTestVariableResultHeader.builder().resultId(resultId).resultHeader(newResultHeader).build());
        }
    }

    @Override
    public String findHeaderByResultId(Long resultId) {
        VarProcessTestVariableResultHeader varProcessTestVariableResultHeader = varProcessTestVariableResultHeaderMapper.selectOne(Wrappers.<VarProcessTestVariableResultHeader>lambdaQuery()
                .eq(VarProcessTestVariableResultHeader::getResultId, resultId));
        if (varProcessTestVariableResultHeader == null) {
            return StringPool.EMPTY;
        }
        return varProcessTestVariableResultHeader.getResultHeader();
    }
}
