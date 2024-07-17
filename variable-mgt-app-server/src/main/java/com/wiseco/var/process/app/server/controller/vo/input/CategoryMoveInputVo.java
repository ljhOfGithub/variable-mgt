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

@Data
public class CategoryMoveInputVo {

    @Schema(description = "分类id")
    @NotNull(message = "请输入分类id")
    private Long categoryId;

    @Schema(description = "上移/下移",allowableValues = "UP,DOWN")
    @NotNull(message = "请指定上移 or 下移")
    private OpeType opeType;
}
