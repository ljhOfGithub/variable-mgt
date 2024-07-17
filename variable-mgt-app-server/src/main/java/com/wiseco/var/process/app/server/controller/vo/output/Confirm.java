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

/**
 * @author xupei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "询问用户是否继续：是/否")
public class Confirm {

    @Schema(description = "类型")
    private String type = "CONFIRM";

    @Schema(description = "消息")
    private String msg;

    @Schema(description = "前端是否需要弹窗，默认true")
    private Boolean show = true;

}
