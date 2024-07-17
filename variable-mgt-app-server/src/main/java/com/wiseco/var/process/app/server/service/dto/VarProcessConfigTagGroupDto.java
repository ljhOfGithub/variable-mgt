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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "标签组 DTO")
public class VarProcessConfigTagGroupDto implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "标签组id", example = "1")
    private Long groupId;

    @Schema(description = "标签组名称", example = "身份信息")
    private String groupName;

    @Schema(description = "最后编辑人", example = "张三")
    private String updatedUser;

    @Schema(description = "编辑时间", example = "2022-08-31 10:00:0")
    private Timestamp updatedTime;

    @Schema(description = "标签list", example = "null")
    private List<VarProcessConfigTagDto> tagList;
}
