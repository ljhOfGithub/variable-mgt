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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.var.process.app.server.controller.vo.input.MonitoringResultPageInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringResultPageOutputVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 监控预警 Mapper
 * </p>
 *
 * @author wiseco
 * @since 睿信2.3
 */
@Mapper
public interface VarProcessMonitoringAlertResultMapper extends BaseMapper<MonitoringResultPageOutputVO> {

    /**
     * 分页查询
     *
     * @param page    分页
     * @param inputVO 输入
     * @return 页面
     */
    IPage<MonitoringResultPageOutputVO> getResultPage(Page page, @Param("inputVO") MonitoringResultPageInputVO inputVO);
}
