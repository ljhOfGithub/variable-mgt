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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 变量配置标签输出参数 DTO
 *
 * @author wangxianli
 * @since 2022/9/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量配置标签输出参数")
public class VariableConfigTagOutputDto implements Serializable {

    private static final long serialVersionUID = 8240823771891283410L;

    @Schema(description = "标签组id", example = "1")
    private Long groupId;

    @Schema(description = "标签组名称", example = "身份信息")
    private String groupName;

    @Schema(description = "最后编辑人", required = true, example = "NA,-9999")
    private String updatedUser;

    @Schema(description = "编辑时间", required = true, example = "2022-08-31 10:00:0")
    private Timestamp updatedTime;

}
