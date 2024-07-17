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
package com.wiseco.var.process.app.server.service.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author: fudengkui
 * @since: 2023-02-22 20:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "java工具类编号DTO")
public class JavaToolkitIdentifierDTO implements Serializable {

    @NotEmpty(message = "编号不能为空")
    @Schema(description = "编号", type = "String", example = "0005DAF3E2D7B200")
    private String identifier;

}
