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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 系统日志搜索结果分页出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/5/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统日志搜索结果分页出参 DTO")
public class SysLogPagedOutputDto {

    @Schema(description = "记录列表")
    private List<SysLogVo> records;

    @Schema(description = "当前页码")
    private Long currentPageNo;

    @Schema(description = "总页数")
    private Long totalPageNumber;

    @Schema(description = "总记录数量")
    private Long totalRecordNumber;

    /**
     * 系统日志 VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "系统日志 VO")
    public static class SysLogVo {

        @Schema(description = "操作时间")
        private String actionTime;

        @Schema(description = "用户名")
        private String userName;

        @Schema(description = "姓名")
        private String fullName;

        @Schema(description = "操作类型")
        private String actionType;

        @Schema(description = "日志描述")
        private String description;

        @Schema(description = "操作结果")
        private String actionResult;
        /*
        @Schema(description = "请求耗时 (ms)", position = 100)
        private Integer requestTimeCost;

        @Schema(description = "浏览器客户端", position = 101)
        private String client;

        @Schema(description = "IP地址", position = 102)
        private String ipAddress;
         */
    }
}
