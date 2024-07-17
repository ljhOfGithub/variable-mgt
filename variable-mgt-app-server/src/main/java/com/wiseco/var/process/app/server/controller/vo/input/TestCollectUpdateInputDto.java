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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author: wangxianli
 */
@Schema(description = "组件测试数据集修改DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestCollectUpdateInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "数据集ID", required = true, example = "1")
    @NotNull(message = "数据集ID不能为空")
    private Long id;

    @Schema(description = "数据集名称", required = true, example = "测试数据集")
    @Size(max = 100, message = "数据集名称不得超过100字符!")
    @NotNull(message = "数据集名称不能为空")
    private String name;

    @Schema(description = "备注", example = "无")
    @Size(max = 500, message = "描述不得超过500字符!")
    private String remark;

}
