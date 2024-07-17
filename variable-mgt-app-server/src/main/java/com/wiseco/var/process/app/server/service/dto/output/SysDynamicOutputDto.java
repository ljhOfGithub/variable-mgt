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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangxianli
 * @since 2022/3/9
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "动态信息查询结果DTO")
public class SysDynamicOutputDto implements Serializable {

    @Schema(description = "动态Id", example = "1")
    private Long id;

    @Schema(description = "空间类别", example = "张三")
    private String spaceType;

    @Schema(description = "空间业务ID", example = "1")
    private Long spaceBusinessId;

    @Schema(description = "操作类型", example = "张三")
    private String operateType;

    @Schema(description = "策略ID", example = "1")
    private Long strategyId;

    @Schema(description = "业务类型", example = "张三")
    private String businessType;

    @Schema(description = "业务细分类型", example = "张三")
    private String businessBucket;

    @Schema(description = "业务Id", example = "1")
    private String businessId;

    @Schema(description = "创建人", example = "张三")
    private String createdUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2021-10-10 10:00:0")
    private Date createdTime;

    @Schema(description = "相对时间", example = "1分钟以前")
    private String dateString;

    @Schema(description = "业务路径", example = "张三")
    private String businessPath;

    @Schema(description = "业务描述", example = "张三")
    private String businessRemark;

}
