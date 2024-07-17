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
package com.wiseco.var.process.app.server.controller.vo.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 生产数据导入查询条件输入参数 DTO
 * ("生产数据"指经过决策生产出的数据)
 * </p>
 * <p>
 * 使用于决策服务 开始测试 生产数据导入
 * </p>
 *
 * @author Zhaoxiong Chen
 * @since 2022/2/14
 */
@Schema(description = "生产数据导入查询条件输入参数 DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestProducedDataSearchInputDto implements Serializable {
    /**
     * 分页查询设置
     */
    @Schema(description = "每页数据行数", example = "10")
    private Integer pageSize = MagicNumbers.FIVE;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNo = 1;

    /**
     * 生产数据查询条件设置
     */
    @Schema(description = "变量清单ID", required = true, example = "1")
    @NotNull
    private Long manifestId;

    @Schema(description = "执行开始时间", example = "1970-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executionIntervalStart;

    @Schema(description = "执行结束时间", example = "2000-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executionIntervalEnd;

    @Schema(description = "执行状态是否成功", example = "成功, 失败, ...")
    private String executionStatus;
}
