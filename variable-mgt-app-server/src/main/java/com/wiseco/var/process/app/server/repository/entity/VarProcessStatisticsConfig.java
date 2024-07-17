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

import java.time.LocalDateTime;

/**
 * <p>
 * 统计分析配置表
 * </p>
 *
 * @author yaoshun
 * @since 2023-09-11
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_statistics_config")
public class VarProcessStatisticsConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 实时服务ID
     */
    @TableField("var_process_service_id")
    private Long varProcessServiceId;

    /**
     * 变量清单ID
     */
    @TableField("var_process_manifest_id")
    private Long varProcessManifestId;

    /**
     * 开始时间
     */
    @TableField("start_date")
    private LocalDateTime startDate;

    /**
     * 结束时间
     */
    @TableField("end_date")
    private LocalDateTime endDate;

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
