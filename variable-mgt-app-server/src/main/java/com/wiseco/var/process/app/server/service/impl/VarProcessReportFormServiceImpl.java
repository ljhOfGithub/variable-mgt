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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.controller.vo.ReportFormItemVo;
import com.wiseco.var.process.app.server.repository.VarProcessReportFormMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessReportForm;
import com.wiseco.var.process.app.server.service.monitoring.VarProcessReportFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 监控预警的报表的业务逻辑层接口实现类
 */

@Service
public class VarProcessReportFormServiceImpl extends ServiceImpl<VarProcessReportFormMapper, VarProcessReportForm> implements VarProcessReportFormService {

    @Autowired
    private VarProcessReportFormMapper varProcessReportFormMapper;

    /**
     * 获取服务报表的list
     * @param deptCodes 部门的list
     * @param userNames 用户的list
     * @return 服务报表的list
     */
    @Override
    public List<ReportFormItemVo> getServiceReportFormList(List<String> deptCodes, List<String> userNames) {
        return varProcessReportFormMapper.getServiceReportFormList(deptCodes, userNames);
    }

    /**
     * 获取单指标分析报表的list
     * @param deptCodes 部门的list
     * @param userNames 用户的list
     * @return 单指标分析报表的list
     */
    @Override
    public List<ReportFormItemVo> getVariableReportFormList(List<String> deptCodes, List<String> userNames) {
        return varProcessReportFormMapper.getVariableReportFormList(deptCodes, userNames);
    }

    /**
     * 获取指标对比分析报表的list
     * @param deptCodes 部门的list
     * @param userNames 用户的list
     * @return 指标对比分析报表的list
     */
    @Override
    public List<ReportFormItemVo> getVariableCompareReportFromList(List<String> deptCodes, List<String> userNames) {
        return varProcessReportFormMapper.getVariableCompareReportFromList(deptCodes, userNames);
    }
}
