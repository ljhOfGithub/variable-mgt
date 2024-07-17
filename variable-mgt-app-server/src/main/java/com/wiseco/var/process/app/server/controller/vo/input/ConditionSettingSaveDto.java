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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: liusiyu
 * @date: 2024/2/27
 * @Time: 10:58
 */
@Schema(description = "保存查询条件，表头列 入参DTO")
@Data
public class ConditionSettingSaveDto {

    @Schema(description = "查询条件id", example = "1")
    @NotNull(message = "查询条件id不能为空")
    private Long id;

    @Schema(description = "是否展示,0：否，1：是", example = "0")
    private Integer display;

    @Schema(description = "排序权重，越小排序越靠前", example = "1")
    @Min(value = 1, message = "排序权重必须大于0")
    private Integer weight;

    @Schema(description = "是否锁定,0：否，1：是", example = "0")
    private Integer isLock;

    @Schema(description = "是否锁定在列头,0：否，1：是", example = "0")
    private Integer isLockHead;
}
