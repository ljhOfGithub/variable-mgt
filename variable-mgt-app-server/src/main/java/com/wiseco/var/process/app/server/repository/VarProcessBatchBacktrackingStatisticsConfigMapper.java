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
package com.wiseco.var.process.app.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingStatisticsConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 批量回溯分析配置表 Mapper 接口
 * </p>
 *
 * @author yaoshun
 * @since 2023-09-11
 */
public interface VarProcessBatchBacktrackingStatisticsConfigMapper extends BaseMapper<VarProcessBatchBacktrackingStatisticsConfig> {

    /**
     * 获取有权限的统计配置
     * @param deptCodes 部门codes
     * @param userNames 用户名
     * @return list
     */
    List<Long> findAccessConfig(@Param("deptCodes") List<String> deptCodes, @Param("userNames") List<String> userNames);
}
