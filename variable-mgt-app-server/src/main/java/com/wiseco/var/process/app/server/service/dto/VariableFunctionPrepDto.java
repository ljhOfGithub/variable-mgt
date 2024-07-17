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
 * 预处理逻辑实体类
 *
 * @author wangxianli
 * @since 2022/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "预处理逻辑 DTO")
public class VariableFunctionPrepDto implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "函数ID", example = "1")
    private Long functionId;

    @Schema(description = "编号", example = "")
    private String identifier;

    @Schema(description = "函数名", example = "")
    private String name;

    @Schema(description = "预处理对象", example = "")
    private String prepObjectName;

    @Schema(description = "函数内容", example = "")
    private String content;

    @Schema(description = "创建部门")
    private String dept;
}
