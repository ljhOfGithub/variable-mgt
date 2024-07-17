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

import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessLog;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@MessageCondition.OnMongoMessageEnabled
public class MongoProductDataServiceImpl implements ProductDataService {

    @Autowired
    private SimpleMongoService<MongoVarProcessLog> mongoService;


    @Override
    public List<MongoVarProcessLog> selectByCondition(Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions, Class<MongoVarProcessLog> varProcessLogClass, Integer pageNo, Integer pageSize, String field) {
        return mongoService.selectByCondition(CommonConstant.MONGO_VAR_PROCESS_LOG,exactConditions,dateConditions,varProcessLogClass,pageNo,pageSize,field);
    }

    @Override
    public Long countByCondition(Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions) {
        return mongoService.countByCondition(CommonConstant.MONGO_VAR_PROCESS_LOG,exactConditions,dateConditions);
    }
}
