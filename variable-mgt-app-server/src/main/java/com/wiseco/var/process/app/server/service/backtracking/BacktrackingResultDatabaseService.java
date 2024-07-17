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
package com.wiseco.var.process.app.server.service.backtracking;

import com.wiseco.var.process.app.server.commons.util.StringUtils;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingTaskListDetailVO;
import com.wiseco.var.process.app.server.service.DbOperateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xupei
 */
@Service
public class BacktrackingResultDatabaseService {
    @Resource
    private DbOperateService dbOperateService;

    private static final String VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT = "var_process_batch_backtracking_task_result";

    /**
     * count
     *
     * @param tableName 表名
     * @param filter    查询条件
     * @return count
     */
    public long count(String tableName, Map<String, Object> filter) {
        Object[] args = new Object[filter.size()];
        final String sql = "SELECT count(*)" + buildSql(tableName, filter, args);
        return dbOperateService.queryForLong(sql, args, Long.class);
    }


    /**
     * 获取任务完成条数
     *
     * @param taskId 任务id
     * @return 任务完成条数
     */
    public long getTaskCompleteTotal(Long taskId) {
        return dbOperateService.queryForLong("SELECT count(*)  FROM " + VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT + " where status != 'NOT_EXECUTED' AND task_id = " + taskId,Long.class);
    }


    /**
     * 最大值
     *
     * @param tableName 表名
     * @param colum     字段
     * @param filter    条件
     * @return 最大值
     */
    public long max(String tableName, String colum, Map<String, Object> filter) {
        Object[] args = new Object[filter.size()];
        final String sql = buildSql(tableName, filter, args);
        String query = "SELECT max(" + colum + ")" + sql;
        return dbOperateService.queryForLong(query, args, Long.class);
    }

    /**
     * 最小值
     *
     * @param tableName 表名
     * @param colum     字段
     * @param filter    条件
     * @return 最小值
     */
    public long min(String tableName, String colum, Map<String, Object> filter) {
        Object[] args = new Object[filter.size()];
        final String sql = buildSql(tableName, filter, args);
        String query = "SELECT min(" + colum + ")" + sql;
        return dbOperateService.queryForLong(query, args, Long.class);
    }

    /**
     * 求均值
     *
     * @param tableName 表名
     * @param colum     字段
     * @param filter    过滤条件
     * @return 均值
     */
    public float avg(String tableName, String colum, Map<String, Object> filter) {
        Object[] args = new Object[filter.size()];
        final String sql = buildSql(tableName, filter, args);
        String query = "SELECT avg(" + colum + ")" + sql;
        return dbOperateService.queryForFloat(query, args);
    }

    /**
     * 构建sql
     *
     * @param tableName 表名
     * @param filter    过滤条件
     * @param data      参数
     * @return sql语句
     */
    private String buildSql(String tableName, Map<String, Object> filter, Object[] data) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" FROM ").append(tableName).append(" WHERE 1=1");
        if (filter != null) {
            int i = 0;
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                String key = entry.getKey();
                key = StringUtils.convertCamelCaseToUnderscore(key);
                Object value = entry.getValue();
                sqlBuilder.append(" AND ").append(key).append(" = ?");
                data[i++] = value;
            }
        }
        return sqlBuilder.toString();
    }

    /**
     * 查看详情
     *
     * @param resultCode 结果的编码
     * @return 执行记录查看列表VO
     */
    public BacktrackingTaskListDetailVO getResultDetail(String resultCode) {
        BacktrackingTaskListDetailVO backtrackingTaskListDetailVO = new BacktrackingTaskListDetailVO();
        String requestInfo = null;
        String engineInfo = null;
        String responseInfo = null;
        String exceptionInfo = null;
        String sql = "SELECT request_info, engine_info, response_info, exception_info FROM " + VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT + " WHERE code = '" + resultCode + "'";
        List<Map<String, Object>> queryResult = dbOperateService.queryForList(sql);

        for (Map<String, Object> row : queryResult) {
            requestInfo = (String) row.get("request_info");
            engineInfo = (String) row.get("engine_info");
            responseInfo = (String) row.get("response_info");
            String exception = (String) row.get("exception_info");
            if (!"null".equals(exception)) {
                exceptionInfo = exception;
            }
        }

        backtrackingTaskListDetailVO.setRequestInfo(requestInfo);
        backtrackingTaskListDetailVO.setEngineInfo(engineInfo);
        backtrackingTaskListDetailVO.setResponseInfo(responseInfo);
        backtrackingTaskListDetailVO.setExceptionInfo(exceptionInfo);
        return backtrackingTaskListDetailVO;
    }
}
