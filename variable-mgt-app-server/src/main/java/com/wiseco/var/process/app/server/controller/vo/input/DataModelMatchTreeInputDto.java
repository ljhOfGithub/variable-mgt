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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author: wangxianli
 */
@Schema(description = "变量匹配树参数DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DataModelMatchTreeInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", required = true, example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long spaceId;

    @Schema(description = "数据类型", required = true, example = "int, double, boolean, string, date, datetime, object")
    @NotEmpty(message = "数据类型为空")
    private String varType;

    @Schema(description = "数据类型", example = "[\"int\", \"double\", \"boolean\", \"string\", \"date\", \"datetime\", \"object\", \"array\"]")
    private List<String> varTypeList;

    @Schema(description = "是否数组", example = "true")
    private Boolean isArrAy;

    @Schema(description = "直接映射左值", example = "xp_test_5.arr7.arr7a1")
    private String leftProperty;

}

