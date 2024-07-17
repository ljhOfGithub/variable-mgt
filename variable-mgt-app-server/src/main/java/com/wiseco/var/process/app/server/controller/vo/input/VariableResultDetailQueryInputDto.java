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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量结果详情查询入参 DTO
 * <p>适用于报文和变量表详情查询</p>
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "实时服务结果详情查询输入参数")
public class VariableResultDetailQueryInputDto implements Serializable {

    private static final long serialVersionUID = -7059743880217848234L;

    @Schema(description = "REST 生成流水号")
    private String restSerialNo;

    @Schema(description = "引擎生成流水号")
    private String engineSerialNo;
}
