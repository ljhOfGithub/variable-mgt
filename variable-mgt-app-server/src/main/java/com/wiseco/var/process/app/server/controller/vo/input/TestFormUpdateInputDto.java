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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author: wangxianli
 */
@Schema(description = "测试集明细数据修改DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestFormUpdateInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "数据修改类型：add-新增，update-修改，delete-删除，copy-复制", example = "add")
    private String updateType;

    @Schema(description = "测试集ID", required = true, example = "1")
    @NotNull(message = "测试集ID不能为空")
    private Long testId;

    @Schema(description = "变量空间ID", required = true, example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long spaceId;

    @Schema(description = "测试类型：1-变量，2-公共函数，3-服务接口", example = "1")
    private Integer testType;

    @Schema(description = "变量/公共函数ID/接口ID", example = "1")
    private Long id;

    @Schema(description = "测试数据Id：修改有值", example = "0")
    private String dataId;

    @Schema(description = "预期结果变量全路径list，包含的变量：有预期结果的情况下传入", example = "[\"input.application\"]")
    private List<String> expectHeader;

    @Schema(description = "输入数据", example = "null")
    private JSONObject inputData;

    @Schema(description = "预期结果数据", example = "null")
    private JSONObject expectData;

}

