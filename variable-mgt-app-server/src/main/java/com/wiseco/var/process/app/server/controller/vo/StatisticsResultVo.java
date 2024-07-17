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
package com.wiseco.var.process.app.server.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 统计分析配置表
 * </p>
 *
 * @author yaoshun
 * @since 2023-09-11
 */
@Schema(description = "统计结果 vo")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StatisticsResultVo implements Serializable {
    private static final long serialVersionUID = 8799865908944970009L;

    /**
     * 变量code
     */
    private String varCode;

    /**
     * 变量名称
     */
    private String varName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * iv值
     */
    private BigDecimal ivResult;

    /**
     * psi值
     */
    private BigDecimal psiResult;

    /**
     * 缺失值占比
     */
    private BigDecimal missingRatio;

    /**
     * 唯一值数量
     */
    private Integer uniqueNum;

    /**
     * 唯一值
     */
    private String uniqueVal;

    /**
     * 特殊值占比
     */
    private BigDecimal specialRatio;

    /**
     * 零值占比
     */
    private BigDecimal zeroRatio;

    /**
     * 最小值
     */
    private BigDecimal minimumVal;

    /**
     * 最大值
     */
    private BigDecimal maxVal;

    /**
     * 均值
     */
    private BigDecimal averageVal;

    /**
     * 分位数结果
     */
    private String percentageResult;

}
