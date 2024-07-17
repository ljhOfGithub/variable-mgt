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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema(description = "审核参数入参Dto")
@AllArgsConstructor
@NoArgsConstructor
public class VarProcessParamInputDto implements Serializable {
    private static final long serialVersionUID = 12897391719372L;

    @Schema(description = "参数编码")
    private String code;

    @Schema(description = "审核参数是否开启, 0关闭 1开启")
    private Integer isEnabled;

}
