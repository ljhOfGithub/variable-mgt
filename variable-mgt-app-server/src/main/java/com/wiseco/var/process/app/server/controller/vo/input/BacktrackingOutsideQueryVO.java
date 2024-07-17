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

import com.wiseco.boot.data.PageDTO;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "外数服务调用查看列表 VO")
public class BacktrackingOutsideQueryVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "执行状态")
    private BacktrackingTaskStatusEnum taskStatus;

    @Schema(description = "是否查得")
    private Boolean whetherFound;

    // todo 完成剩下

}
