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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessStatisticsConfig;

import java.util.List;

/**
 * <p>
 * 统计分析配置表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessStatisticsConfigService extends IService<VarProcessStatisticsConfig> {

    /**
     * 寻找当前的统计数据
     * @return 当前的统计数据
     */
    List<VarProcessStatisticsConfig> findCurrentStatistics();

    /**
     * 获取有权限的统计配置
     * @return list
     */
    List<Long> findAccessConfig();
}
