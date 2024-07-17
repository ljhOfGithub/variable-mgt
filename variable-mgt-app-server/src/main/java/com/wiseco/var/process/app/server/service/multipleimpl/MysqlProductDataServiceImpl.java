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

import com.google.common.base.CaseFormat;
import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessLog;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@MessageCondition.OnMysqlMessageEnabled
public class MysqlProductDataServiceImpl implements ProductDataService {

    @Autowired
    @Qualifier("internalJdbcTemplate")
    private JdbcTemplate internalJdbcTemplate;

    @Override
    public List<MongoVarProcessLog> selectByCondition(Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions, Class<MongoVarProcessLog> varProcessLogClass, Integer pageNo, Integer pageSize, String field) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append("var_process_log_message").append(" WHERE 1=1 ");

        // 构建精确查询条件
        exactConditions.forEach((key, value) -> sql.append("AND ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key)).append(" = ? "));

        // 构建日期范围查询条件
        dateConditions.forEach((columnName, scopePair) -> {
            if (scopePair.getKey() != null && scopePair.getValue() != null) {
                sql.append("AND ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName)).append(" BETWEEN ? AND ? ");
            } else if (scopePair.getKey() != null) {
                sql.append("AND ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName)).append(" >= ? ");
            } else if (scopePair.getValue() != null) {
                sql.append("AND ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName)).append(" <= ? ");
            }
        });

        // 添加排序
        if (!StringUtils.isEmpty(field)) {
            sql.append("ORDER BY ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field)).append(" DESC ");
        }

        // 添加分页
        sql.append("LIMIT ? , ? ");

        Object[] params = buildParams(exactConditions, dateConditions, pageNo, pageSize);

        return internalJdbcTemplate.query(sql.toString(), params, (rs,rowCol) -> {
            MongoVarProcessLog mongoVarProcessLog = new MongoVarProcessLog();
            mongoVarProcessLog.setServiceId(rs.getString("service_id"));
            return mongoVarProcessLog;
        });
    }

    /**
     * 构建参数数组
     * @param exactConditions 具体查询条件
     * @param dateConditions 日期查询条件
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return 参数数组
     */
    private Object[] buildParams(Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions, Integer currentPage, Integer pageSize) {

        // 添加精确查询条件的参数值
        List<Object> params = new ArrayList<>(exactConditions.values());

        // 添加日期范围查询条件的参数值
        dateConditions.forEach((columnName, scopePair) -> {
            if (scopePair.getKey() != null) {
                params.add(scopePair.getKey());
            }
            if (scopePair.getValue() != null) {
                params.add(scopePair.getValue());
            }
        });

        // 添加分页参数
        params.add((currentPage - 1) * pageSize);
        params.add(pageSize);

        return params.toArray();
    }

    @Override
    public Long countByCondition(Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM " + "var_process_log_message" + " WHERE 1 = 1");

        // 构建精确查询条件
        exactConditions.forEach((key, value) -> sql.append(" AND ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key)).append(" = ?"));

        // 构建日期范围查询条件
        dateConditions.forEach((columnName, scopePair) -> {
            if (scopePair.getKey() != null && scopePair.getValue() != null) {
                sql.append(" AND ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName)).append(" >= ? AND ")
                        .append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName)).append(" <= ?");
            } else if (scopePair.getKey() != null) {
                sql.append(" AND ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName)).append(" >= ?");
            } else if (scopePair.getValue() != null) {
                sql.append(" AND ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName)).append(" <= ?");
            }
        });

        Object[] exactConditionsArray = exactConditions.values().toArray();
        Object[] dateConditionsArray = dateConditions.values().stream()
                .flatMap(pair -> Stream.of(pair.getKey(), pair.getValue()))
                .filter(Objects::nonNull)
                .toArray();
        Object[] conditionsArray = Stream.concat(Arrays.stream(exactConditionsArray), Arrays.stream(dateConditionsArray))
                .toArray(Object[]::new);
        // 计算总数
        return internalJdbcTemplate.queryForObject(sql.toString(), Long.class,conditionsArray);
    }
}
