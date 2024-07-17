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

/**
 * 变量、公共函数基础信息
 *
 * @author wangxianli
 * @since 2022/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量、公共函数信息 DTO")
public class VariableBaseDetailDto implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量/公共函数ID/接口ID", example = "1")
    private Long id;

    @Schema(description = "编号", example = "")
    private String identifier;

    @Schema(description = "变量名", example = "")
    private String name;

    @Schema(description = "变量中文名", example = "")
    private String label;

    @Schema(description = "数据类型", example = "")
    private String dataType;

    @Schema(description = "版本", example = "1")
    private Integer version;

    @Schema(description = "状态：1-编辑中，2-上架，3-下架", example = "1")
    private Integer status;

}
