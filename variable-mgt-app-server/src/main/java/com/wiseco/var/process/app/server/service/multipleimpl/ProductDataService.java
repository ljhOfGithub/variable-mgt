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

import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessLog;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * 生产数据查询 和 导入service
 */
public interface ProductDataService {

    /**
     * 分页条件查询
     * @param exactConditions 具体条件
     * @param dateConditions 日期条件
     * @param varProcessLogClass 类型
     * @param pageNo 页码
     * @param pageSize 页面大小
     * @param field 字段名
     * @return VarProcessLog的List
     */
    List<MongoVarProcessLog> selectByCondition(Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions, Class<MongoVarProcessLog> varProcessLogClass, Integer pageNo, Integer pageSize, String field);

    /**
     * 统计数量
     * @param exactConditions 具体条件
     * @param dateConditions 日期条件
     * @return 数量
     */
    Long countByCondition(Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions);

}
