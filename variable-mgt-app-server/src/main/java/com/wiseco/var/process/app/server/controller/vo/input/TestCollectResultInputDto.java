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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 测试执行结果分页查询输入参数 DTO
 *
 * @author wangxianli
 * @since 2021/11/30 19:29
 */
@Schema(description = "测试执行结果分页查询DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestCollectResultInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "执行结果ID", required = true, example = "1")
    @NotNull(message = "执行结果ID不能为空")
    private Long id;

    @Schema(description = "页码", required = true, example = "1")
    @NotNull(message = "页码不能为空")
    private Integer page;

    @Schema(description = "分页个数", required = true, example = "10")
    @NotNull(message = "分页个数不能为空")
    private Integer size;

    /**
     * 测试结果状态查询
     */
    @Schema(description = "状态", required = true, example = "0-全部，1-正常，2-异常，3-预期一致，4-预期不一致")
    @NotNull(message = "状态")
    private String state;

    @Schema(description = "测试数据集的类型, 1-变量定义; 2-数据预处理、变量模板、公共方法; 3.变量清单", example = "3")
    @NotNull(message = "测试数据集的类型不能为空")
    private Integer testType;
}
