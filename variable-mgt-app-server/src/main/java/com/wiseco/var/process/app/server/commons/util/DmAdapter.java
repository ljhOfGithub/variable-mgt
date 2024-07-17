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
package com.wiseco.var.process.app.server.commons.util;

import com.wiseco.var.process.app.server.commons.constant.DbTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * dm数据库适配
 */
@Service
public class DmAdapter {

    public static final String GROUP = "group";

    public static final String BY = "by";
    @Autowired
    @Qualifier("internalJdbcTemplate")
    private JdbcTemplate internalJdbcTemplate;

    @Autowired
    @Qualifier("configDatasourceTemplate")
    private JdbcTemplate configDatasourceTemplate;

    @Value("${spring.datasourcetype:mysql}")
    private String dataSourceType;

    /**
     * 动态表前缀
     */
    private static final String DYNAMIC_TABLE_PREFIX = "var_process_manifest_";

    private static final String VAR_PROCESS_MANIFEST_HEADER = "var_process_manifest_header";

    private static final String REGULAR_EXPRESSION = ".*\\b%s\\b.*";

    /**
     * 修改会话级别分组配置sql
     */
    private static final String SF_SET_SESSION_PARA_VALUE_SQL = "SF_SET_SESSION_PARA_VALUE ('GROUP_OPT_FLAG',1);";

    private static final String QUERY_TABLE_COLUM_SQL = "SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME='%S'";

    /**
     * 修改会话级别分组配置——业务库
     */
    public void modifyGroupOptFlagOfConfigJdbc() {
        if (DbTypeConstant.DM.equals(dataSourceType)) {
            configDatasourceTemplate.execute(SF_SET_SESSION_PARA_VALUE_SQL);
        }
    }


    /**
     * 修改会话级别分组配置——内数库
     */
    public void modifyGroupOptFlagOfInternalJdbc() {
        if (DbTypeConstant.DM.equals(dataSourceType)) {
            internalJdbcTemplate.execute(SF_SET_SESSION_PARA_VALUE_SQL);
        }
    }

    /**
     * 修改会话级别分组配置——内数库
     *
     * @param sql sql
     */
    public void modifyGroupOptBySql(String sql) {
        String toLowerCaseSql = sql.toLowerCase();
        //如果sql能匹配group by则修改分组配置
        if (toLowerCaseSql.matches(String.format(REGULAR_EXPRESSION, GROUP.toLowerCase())) && toLowerCaseSql.matches(String.format(REGULAR_EXPRESSION, BY.toLowerCase()))) {
            modifyGroupOptFlagOfInternalJdbc();
        }
    }


    /**
     * mapGet忽略大小写
     *
     * @param map map
     * @param key key
     * @return java.lang.Object
     */
    public static Object mapGetIgnoreCase(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return map.get(key.toUpperCase()) != null ? map.get(key.toUpperCase()) : map.get(key.toLowerCase());
    }


    /**
     * 列名转换
     *
     * @param tableName 表名
     * @param colum     字段名
     * @return java.lang.String
     */
    public  String columConvert(String tableName, String colum) {
        if (StringUtils.isEmpty(colum)) {
            return colum;
        }

        if (isDynamicTable(tableName)) {
            return "\"" + colum + "\"";
        } else {
            return colum;
        }
    }

    /**
     * 列名转换——字段集合
     *
     * @param tableName 表名
     * @param columList 字段列
     * @return java.util.List<java.lang.String>
     */
    public List<String> columConvert(String tableName, List<String> columList) {
        if (CollectionUtils.isEmpty(columList)) {
            return columList;
        }

        if (isDynamicTable(tableName)) {
            return columList.stream().map(item -> "\"" + item + "\"").collect(Collectors.toList());
        } else {
            return new ArrayList<>(columList);
        }
    }

    /**
     * 条件转换-sql
     *
     * @param tableName 表名
     * @param sql       sql
     * @return java.lang.Object
     */
    public String conditionConvert(String tableName, String sql) {
        if (StringUtils.isEmpty(sql) || !isDynamicTable(tableName)) {
            return sql;
        }

        List<String> tableColum = getColumByTableName(tableName);

        for (String colum : tableColum) {
            //使用正则表达式判断sql是否存在colum，且前后不能存在字母、数字、下划线
            if (sql.matches(String.format(REGULAR_EXPRESSION, colum))) {
                sql = sql.replace(colum, "\"" + colum + "\"");
            }
        }
        return sql;
    }


    /**
     * 获取表列名
     *
     * @param tableName 表
     * @return java.util.List
     */
    public List<String> getColumByTableName(String tableName) {
        List<String> tableColum = internalJdbcTemplate.queryForList(String.format(QUERY_TABLE_COLUM_SQL, tableName))
                .stream()
                .map(item -> item.get("COLUMN_NAME").toString()).collect(Collectors.toList());
        return tableColum;
    }

    /**
     * 判断是否是动态表
     *
     * @param tableName 表名
     * @return java.lang.Object
     */
    private  boolean isDynamicTable(String tableName) {
        return tableName.startsWith(DYNAMIC_TABLE_PREFIX) && !VAR_PROCESS_MANIFEST_HEADER.equals(tableName);
    }

}
