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

import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableResultHeader;
import com.wiseco.var.process.app.server.repository.mongodb.repository.VarProcessTestVariableResultHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@MessageCondition.OnMongoMessageEnabled
public class MongoVarProcessTestVariableResultHeaderServiceImpl implements VarProcessTestVariableResultHeaderService {
    @Autowired
    VarProcessTestVariableResultHeaderRepository varProcessTestVariableResultHeaderRepository;

    @Override
    public String findHeaderByResultId(Long resultId) {
        Optional<MongoVarProcessTestVariableResultHeader> optional = varProcessTestVariableResultHeaderRepository.findByResultId(resultId);
        return optional.map(MongoVarProcessTestVariableResultHeader::getResultHeader).orElse(null);
    }

    @Override
    public void saveOrUpdateHeader(Long resultId, String resultHeader) {
        // 根据执行结果 ID 查找表头记录
        Optional<MongoVarProcessTestVariableResultHeader> result = varProcessTestVariableResultHeaderRepository.findByResultId(resultId);
        if (result.isPresent()) {
            // 已经存在表头记录: 更新
            MongoVarProcessTestVariableResultHeader document = result.get();
            document.setResultHeader(resultHeader);
            varProcessTestVariableResultHeaderRepository.save(document);
        } else {
            // 不存在表头记录: 保存
            MongoVarProcessTestVariableResultHeader newDocument = MongoVarProcessTestVariableResultHeader.builder()
                    .resultId(resultId)
                    .resultHeader(resultHeader)
                    .build();
            varProcessTestVariableResultHeaderRepository.save(newDocument);
        }
    }
}
