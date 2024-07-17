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

import java.math.BigDecimal;

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
@TableName("var_process_statistics_result")
public class VarProcessStatisticsResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 统计分析ID
     */
    @TableField("statistics_config_id")
    private Long statisticsConfigId;

    /**
     * 指标名称
     */
    @TableField("var_name")
    private String varName;

    /**
     * 数据类型
     */
    @TableField("data_type")
    private String dataType;

    /**
     * iv值
     */
    @TableField("iv_result")
    private BigDecimal ivResult;

    /**
     * psi值
     */
    @TableField("psi_result")
    private BigDecimal psiResult;

    /**
     * 缺失值占比
     */
    @TableField("missing_ratio")
    private BigDecimal missingRatio;

    /**
     * 唯一值数量
     */
    @TableField("unique_num")
    private Integer uniqueNum;

    /**
     * 唯一值
     */
    @TableField("unique_val")
    private String uniqueVal;

    /**
     * 特殊值占比
     */
    @TableField("special_ratio")
    private BigDecimal specialRatio;

    /**
     * 零值占比
     */
    @TableField("zero_ratio")
    private BigDecimal zeroRatio;

    /**
     * 最小值
     */
    @TableField("minimum_val")
    private BigDecimal minimumVal;

    /**
     * 最大值
     */
    @TableField("max_val")
    private BigDecimal maxVal;

    /**
     * 均值
     */
    @TableField("average_val")
    private BigDecimal averageVal;

    /**
     * 分位数结果
     */
    @TableField("percentage_result")
    private String percentageResult;

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
