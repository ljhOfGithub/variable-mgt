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
import com.wiseco.var.process.app.server.controller.vo.ReportFormItemVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessReportForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监控预警的报表Mapper
 */

@Mapper
public interface VarProcessReportFormMapper extends BaseMapper<VarProcessReportForm> {

    /**
     * 获取服务报表的list
     * @param deptCodes 部门的list
     * @param userNames 用户的list
     * @return 服务报表的list
     */
    List<ReportFormItemVo> getServiceReportFormList(@Param("deptCodes") List<String> deptCodes, @Param("userNames") List<String> userNames);

    /**
     * 获取单指标分析报表的list
     * @param deptCodes 部门的list
     * @param userNames 用户的list
     * @return 单指标分析报表的list
     */
    List<ReportFormItemVo> getVariableReportFormList(@Param("deptCodes") List<String> deptCodes, @Param("userNames") List<String> userNames);

    /**
     * 获取指标对比分析报表的list
     * @param deptCodes 部门的list
     * @param userNames 用户的list
     * @return 指标对比分析报表的list
     */
    List<ReportFormItemVo> getVariableCompareReportFromList(@Param("deptCodes") List<String> deptCodes, @Param("userNames") List<String> userNames);
}
