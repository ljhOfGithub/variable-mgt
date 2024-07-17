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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "通过变量清单中的变量id列表查询对应的数据模型")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VarModelInputDto implements Serializable {

    private static final long serialVersionUID = 4784418045566309633L;

    @Schema(description = "变量清单中包含的变量id的列表")
    List<Long> varIdList;
    @Schema(description = "空间id")
    Long spaceId;
}
