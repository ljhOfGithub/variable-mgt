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
@Schema(description = "字典项出参DTO")
public class DictDetailsOutputDto {

    @Schema(description = "字典项编码", example = "1")
    private String name;

    @Schema(description = "字典项名称", example = "男")
    private String label;

}