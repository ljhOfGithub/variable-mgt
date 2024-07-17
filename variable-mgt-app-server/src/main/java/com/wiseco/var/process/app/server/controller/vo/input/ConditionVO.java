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

import com.wiseco.var.process.app.server.commons.enums.ComparisonOperatorEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mingao
 * @since 2023/9/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "筛选字段条件")
public class ConditionVO {
    @Schema(description = "筛选条件:", example = "EQUALS", required = true)
    private ComparisonOperatorEnum type;

    @Schema(description = "筛选值", required = true)
    private String value;
}
