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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量上架 DTO
 *
 * @author wangxianli
 */
@Schema(description = "变量方案 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariablePlanInputDto implements Serializable {

    private static final long serialVersionUID = 8799865908944973993L;

    @Schema(description = "变量方案id")
    private Long id;

    @Schema(description = "变量模版id")
    private String functionId;

    @Schema(description = "方案名")
    private String planName;

    @Schema(description = "内容", example = "null")
    private JSONObject paramJson;
}
