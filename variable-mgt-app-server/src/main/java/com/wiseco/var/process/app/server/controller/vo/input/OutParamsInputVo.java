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

import com.wiseco.boot.data.PageDTO;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Schema(description = "查询入参对象时的输入Vo")
@NoArgsConstructor
@AllArgsConstructor
public class OutParamsInputVo extends PageDTO implements Serializable {
    private static final long serialVersionUID = -8745995860842618791L;

    @Schema(description = "空间id")
    private Long spaceId;

    @Schema(description = "创建部门id")
    private Long deptId;

    @Schema(description = "对象名/中文名-查询关键字")
    private String keyWords;

    @Schema(description = "已经存在的入参list")
    private List<Long> excludedParams;

    @Schema(description = "数据来源类型")
    private VarProcessDataModelSourceType   sourceType;
}
