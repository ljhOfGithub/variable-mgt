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

import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Map;

public interface SimpleMongoService<T> {

    /**
     * 根据条件查询集合
     *
     * @param collectName 集合名称
     * @param conditions  查询条件，目前查询条件处理的比较简单，仅仅做了相等匹配，没有做模糊查询等复杂匹配
     * @param clazz       对象类型
     * @param currentPage 当前页码
     * @param pageSize    分页大小
     * @param field       倒叙排列字段
     * @return list
     */
    List<T> selectByCondition(String collectName, Map<String, String> conditions, Class<T> clazz, Integer currentPage, Integer pageSize, String field);

    /**
     * 根据条件查询集合
     * 带有日期范围查询功能
     *
     * @param collectName     集合名称
     * @param exactConditions 精确查询条件
     * @param dateConditions  日期查询条件 (闭区间, 允许上界或下界为空)
     * @param clazz           对象类型
     * @param currentPage     当前页码
     * @param pageSize        分页大小
     * @param field           倒叙排序依据的字段名称
     * @return list
     */
    List<T> selectByCondition(String collectName,
                                     Map<String, String> exactConditions,
                                     Map<String, Pair<String, String>> dateConditions,
                                     Class<T> clazz,
                                     Integer currentPage,
                                     Integer pageSize,
                                     String field);

    /**
     * 统计数量
     *
     * @param collectName     集合名称
     * @param exactConditions 精确查询条件
     * @param dateConditions  日期查询条件 (闭区间, 允许上界或下界为空)
     * @return long
     */
    long countByCondition(String collectName, Map<String, String> exactConditions, Map<String, Pair<String, String>> dateConditions);

}
