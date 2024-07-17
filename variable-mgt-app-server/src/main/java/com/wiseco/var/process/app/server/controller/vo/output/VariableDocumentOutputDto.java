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

import java.util.Date;

/**
 * @author: wangxianli
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "策略文档表 出参Dto")
public class VariableDocumentOutputDto {
    @Schema(description = "预览地址")
    private String previewUrl;
    @Schema(description = "空间ID")
    private Long spaceId;
    @Schema(description = "资源ID")
    private Long resourceId;
    @Schema(description = "文档类型")
    private String fileType;
    @Schema(description = "文件原名称")
    private String name;
    @Schema(description = "文件描述")
    private String description;
    @Schema(description = "后缀")
    private String suffix;
    @Schema(description = "文件大小")
    private String size;
    @Schema(description = "创建用户")
    private String createdUser;
    @Schema(description = "创建时间")
    private Date createdTime;
}
