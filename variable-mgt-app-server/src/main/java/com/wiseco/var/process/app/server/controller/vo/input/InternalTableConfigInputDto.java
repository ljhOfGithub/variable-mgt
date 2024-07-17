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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 内部数据表配置
 * </p>
 *
 * @author mingao
 * @since 2023-07-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内部数据表配置入参")
public class InternalTableConfigInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256548598L;

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表中文名")
    private String tableDesc;

    @Schema(description = "使用情况", example = "1")
    private Integer inUse;

    @Schema(description = "逻辑删除标记", example = "1")
    private Integer state;

    @Schema(description = "创建部门")
    private String createdDept;

    @Schema(description = "创建用户")
    private String createdUser;

    @Schema(description = "更新用户")
    private String updatedUser;

    @Schema(description = "创建时间")
    private Date createdTime;

    @Schema(description = "更新时间")
    private Date updatedTime;
}
