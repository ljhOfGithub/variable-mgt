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
 * 执行记录查看列表
 *
 * @author wiseco
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "执行记录查看列表 VO")
public class BacktrackingTaskVO implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "序号")
    private Long sequence;

    @Schema(description = "主体唯一标识")
    private String serialNo;

    // todo 完成剩下

}
