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
package com.wiseco.var.process.app.server.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * 字段sql结果对象
 *
 * @author yangyunsen
 * @since 2022/10/18-22:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldSqlRespVO {
    /**
     * 查询字段sql
     */
    private String fieldSql;

    /**
     * 结果映射map<resultIndex,List<pair<目标字段名称>>>
     */
    private Map<Integer, List<String>> resultIndexTargetPairMap;

    /**
     * 结果字段序号对应的字段显示名和sql别名
     * {1,{first:a.id,second:a_id}}
     */
    private Map<Integer, Pair<String, String>> resultIndexLabelNameAliasMap;
}
