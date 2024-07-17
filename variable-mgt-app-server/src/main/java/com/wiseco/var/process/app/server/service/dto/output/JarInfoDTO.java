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
package com.wiseco.var.process.app.server.service.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: fudengkui
 * @since: 2023-02-21 18:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "jar文件信息DTO")
public class JarInfoDTO implements Serializable {

    @Schema(description = "jar包编号", required = true, type = "String", example = "0005DAF3E2D7B200")
    private String identifier;

    @Schema(description = "jar包名称", required = true, type = "String", example = "0005DAF3E2D7B200")
    private String name;

}
