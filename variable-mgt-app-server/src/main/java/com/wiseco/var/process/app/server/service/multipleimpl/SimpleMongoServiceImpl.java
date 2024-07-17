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
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@MessageCondition.OnMongoMessageEnabled
public class SimpleMongoServiceImpl<T> implements SimpleMongoService<T> {

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;

    /**
     * 功能描述: 分页查询列表信息
     *
     * @param collectName 集合名称
     * @param clazz       对象类型
     * @param currentPage 当前页码
     * @param pageSize    分页大小
     * @return list
     */
    public List<T> selectList(String collectName, Class<T> clazz, Integer currentPage, Integer pageSize) {
        //设置分页参数
        Query query = new Query();
        //设置分页信息
        if (!ObjectUtils.isEmpty(currentPage) && !ObjectUtils.isEmpty(pageSize)) {
            query.limit(pageSize);
            query.skip((long) pageSize * (currentPage - 1));
        }
        return mongoTemplate.find(query, clazz, collectName);
    }

    @Override
    public List<T> selectByCondition(String collectName, Map<String, String> conditions, Class<T> clazz, Integer currentPage, Integer pageSize, String field) {
        if (ObjectUtils.isEmpty(conditions)) {
            return selectList(collectName, clazz, currentPage, pageSize);
        } else {
            //设置分页参数
            Query query = new Query();
            query.limit(pageSize);
            query.skip((long) (currentPage - 1) * pageSize);
            if (!StringUtils.isEmpty(field)) {
                query.with(Sort.by(Sort.Direction.DESC, field));
            }
            // 往query中注入查询条件
            conditions.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));

            return mongoTemplate.find(query, clazz, collectName);
        }
    }

    @Override
    public List<T> selectByCondition(String collectName,
                                     Map<String, String> exactConditions,
                                     Map<String, Pair<String, String>> dateConditions,
                                     Class<T> clazz,
                                     Integer currentPage,
                                     Integer pageSize,
                                     String field) {
        if (ObjectUtils.isEmpty(exactConditions)) {
            // 精准查询条件缺失, 查询全部结果
            return selectList(collectName, clazz, currentPage, pageSize);
        } else if (ObjectUtils.isEmpty(dateConditions)) {
            // 日期范围查询条件缺失, 仅使用精确查询条件
            return selectByCondition(collectName, exactConditions, clazz, currentPage, pageSize, field);
        }

        // 设置分页参数
        Query query = new Query();
        query.limit(pageSize);
        query.skip((long) (currentPage - 1) * pageSize);
        if (!StringUtils.isEmpty(field)) {
            query.with(Sort.by(Sort.Direction.DESC, field));
        }

        // 向 Query 中注入精确查询条件
        exactConditions.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
        // 向 Query 中注入日期范围查询条件
        dateConditions.forEach((columnName, scopePair) -> {
            if (scopePair.getKey() != null && scopePair.getValue() != null) {
                // 添加日期范围: 开始时间和结束时间
                query.addCriteria(Criteria.where(columnName).gte(scopePair.getKey()).lte(scopePair.getValue()));
            } else if (scopePair.getKey() != null) {
                // 开始时间: >=
                query.addCriteria(Criteria.where(columnName).gte(scopePair.getKey()));
            } else if (scopePair.getValue() != null) {
                // 结束时间: <=
                query.addCriteria(Criteria.where(columnName).lte(scopePair.getValue()));
            }
        });
        return mongoTemplate.find(query, clazz, collectName);
    }

    @Override
    public long countByCondition(String collectName, Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions) {
        if (ObjectUtils.isEmpty(exactConditions)) {
            return 0;
        } else {
            Query query = new Query();
            // 向 Query 中注入精确查询条件
            exactConditions.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
            // 向 Query 中注入日期范围查询条件
            dateConditions.forEach((columnName, scopePair) -> {
                if (scopePair.getKey() != null && scopePair.getValue() != null) {
                    // 添加日期范围: 开始时间和结束时间
                    query.addCriteria(Criteria.where(columnName).gte(scopePair.getKey()).lte(scopePair.getValue()));
                } else if (scopePair.getKey() != null) {
                    // 开始时间: >=
                    query.addCriteria(Criteria.where(columnName).gte(scopePair.getKey()));
                } else if (scopePair.getValue() != null) {
                    // 结束时间: <=
                    query.addCriteria(Criteria.where(columnName).lte(scopePair.getValue()));
                }
            });
            return mongoTemplate.count(query, collectName);
        }
    }

}

