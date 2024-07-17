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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量对比 输出参数 DTO
 *
 * @author kangyankun
 * @since 2022/09/22 19:45
 */

@Schema(description = "变量对比OUT_DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableCompareOutputDto implements Serializable {

    private static final long serialVersionUID = 8668690652316747092L;

    @Schema(description = "原始变量信息")
    private VariableCompareDetailOutputDto sourceVariableDetailInfo;

    @Schema(description = "目标变量信息")
    private VariableCompareDetailOutputDto targetVariableDetailInfo;
}
