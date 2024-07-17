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
package com.wiseco.var.process.app.server.service.backtracking.param;

import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * 数据库取值方式任务
 *
 * @author wuweikang
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaskByDbDto {
    /**
     * 流水号
     */
    private String code;
    /**
     * 主体唯一标识分割数组
     */
    private String[] serialNoSpilt;
    /**
     * 总条数
     */
    private int total;
    /**
     * 外数服务取值方式
     */
    private Map<String, String> outsideServiceStrategyMap;
    /**
     * 清单id
     */
    private long manifestId;
    /**
     * 是否开启trace
     */
    private Boolean enableTrace;
    /**
     * 批量回溯名称
     */
    private String backtrackName;
    /**
     * 结果表
     */
    private String resultTable;
    /**
     * 取值条件
     */
    private BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo;
    /**
     * 分页参数
     */
    private int from;
    /**
     * 任务信息
     */
    private VarProcessBatchBacktrackingTask taskInfo;
}
