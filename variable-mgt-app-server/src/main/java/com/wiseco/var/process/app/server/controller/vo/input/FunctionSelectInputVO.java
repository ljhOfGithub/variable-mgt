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

import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "公共函数下拉查询 VO")
public class FunctionSelectInputVO implements Serializable {

    @Schema(description = "空间ID", example = "1")
    @NotNull(message = "空间主键spaceId不能为空")
    private Long spaceId;

    @Schema(description = "函数类型:变量模板，公共方法,数据预处理", example = "1")
    private FunctionTypeEnum functionType;

    @Schema(description = "状态")
    private FlowStatusEnum functionStatus;

    @Schema(description = "返回数据类型")
    private String functionDataType;

}
