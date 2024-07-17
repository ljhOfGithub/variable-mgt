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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author: wangxianli
 */
@Schema(description = "获取在线表单入参DTO")
@Data
public class TestFormTreeInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", required = true, example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long spaceId;

    @Schema(description = "测试类型：1-变量，2-公共函数，3-服务接口", example = "1")
    private Integer testType;

    @Schema(description = "变量/公共函数ID/接口ID", example = "1")
    private Long id;

    @Schema(description = "测试集ID", example = "1")
    private Long testId;

    @Schema(description = "预期结果变量全路径list，需要排除的变量：有预期结果的情况下传入", example = "[\"input.application\"]")
    private List<String> excludeVarList;

    @Schema(description = "预期结果变量全路径list，包含的变量：有预期结果的情况下传入", example = "[\"input.application\"]")
    private List<String> includeVarList;

}
