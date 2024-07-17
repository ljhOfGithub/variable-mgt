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

import java.io.Serializable;

/**
 * 用户代码库入参 DTO
 *
 * @author kangyankun
 * @since 2022/8/31
 */

@Schema(description = "用户代码库DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableCodeBaseQueryInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "用户id", required = true, example = "10000")
    private Long userId;

}
