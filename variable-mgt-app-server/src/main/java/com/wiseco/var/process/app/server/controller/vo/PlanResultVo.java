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
import java.util.List;

/**
 * @author Gmm
 * @since 2023/10/9
 */
@Schema(description = "单一变量生成计划的结果 vo")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PlanResultVo implements Serializable {

    @Schema(description = "生成计划名")
    private String label;

    @Schema(description = "对应数据")
    private List<VariableProduceRecordVo> data;

}
