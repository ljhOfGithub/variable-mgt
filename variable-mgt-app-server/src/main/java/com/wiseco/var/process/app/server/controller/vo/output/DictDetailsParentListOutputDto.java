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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: xiewu
 */
@Data
@Schema(description = "获取字典项的上级字典 出参DTO")
public class DictDetailsParentListOutputDto {

    @Schema(description = "主键id", example = "1")
    private Long id;

    @Schema(description = "字典类型id", example = "1")
    private Long dictId;

    @Schema(description = "字典项编码", example = "yzf")
    private String code;

    @Schema(description = "字典项名称", example = "翼支付")
    private String name;

}
