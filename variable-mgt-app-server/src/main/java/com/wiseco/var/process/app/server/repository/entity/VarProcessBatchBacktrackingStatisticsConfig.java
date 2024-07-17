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
package com.wiseco.var.process.app.server.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 批量回溯分析配置表
 * </p>
 *
 * @author yaoshun
 * @since 2023-09-14
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_batch_backtracking_statistics_config")
public class VarProcessBatchBacktrackingStatisticsConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 批次号ID
     */
    @TableField("batch_number")
    private String batchNumber;

    /**
     * 回溯id
     */
    @TableField("backtracking_id")
    private Long backtrackingId;

    /**
     * 分析指标
     */
    @TableField("analysis_index")
    private String analysisIndex;

    /**
     * iv计算参数配置
     */
    @TableField("iv_config")
    private String ivConfig;

    /**
     * psi计算参数配置
     */
    @TableField("psi_config")
    private String psiConfig;

    /**
     * 特殊值参数配置
     */
    @TableField("special_val_config")
    private String specialValConfig;

    /**
     * 创建用户
     */
    @TableField("created_user")
    private String createdUser;

    /**
     * 更新用户
     */
    @TableField("updated_user")
    private String updatedUser;

}
