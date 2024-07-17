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
package com.wiseco.var.process.app.server.service.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 代码库分页出参 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代码库分页出参 DTO")
public class UserCompnentCodeBasePageOutputDto {
    @Schema(description = "记录列表")
    private List<UserCodeBaseOutputDto> records;

    @Schema(description = "当前页码")
    private Long currentPageNo;

    @Schema(description = "总页数")
    private Long totalPageNumber;

    @Schema(description = "总记录数量")
    private Long totalRecordNumber;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "变量类别输出参数")
    public static class UserCodeBaseOutputDto implements Serializable {

        private static final long serialVersionUID = 8240823771891283410L;

        @Schema(description = "主键id", example = "1")
        private Long id;

        @Schema(description = "用户id", example = "1")
        private Integer userId;

        @Schema(description = "代码块中文名", example = "对规则集复制")
        private String codeBlockName;

        @Schema(description = "代码块内容")
        private String codeBlockContent;

        @Schema(description = "代码块描述")
        private String codeBlockDescribe;

        @Schema(description = "创建时间")
        private String createdTime;

    }
}
