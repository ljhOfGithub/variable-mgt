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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.service.dto.VariableInternalDataServiceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内部数据列表 DTO")
public class VariableInternalDataOutputDto implements Serializable {

    @Schema(description = "变量ID", example = "")
    private Long id;

    @Schema(description = "编号", example = "")
    private String identifier;

    @Schema(description = "变量名", example = "")
    private String name;

    @Schema(description = "对象名", example = "")
    private String objectName;

    @Schema(description = "对象中文名", example = "")
    private String objectLabel;

    @Schema(description = "创建人", example = "张三")
    private String createdUser;

    @Schema(description = "编辑人", example = "张三")
    private String updatedUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2022-06-08 12:00:00")
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后编辑时间", example = "2022-06-08 12:00:00")
    private Date updatedTime;

    @Schema(description = "实时服务列表", example = "null")
    private List<VariableInternalDataServiceDto> serviceList;
}
