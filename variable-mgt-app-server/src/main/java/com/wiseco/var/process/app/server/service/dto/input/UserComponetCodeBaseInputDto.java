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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户代码库配置创建入参 DTO
 *
 * @author kangyankun
 * @since 2022/8/31
 */

@Schema(description = "用户代码库保存DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserComponetCodeBaseInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "主键id", required = false)
    private Integer id;

    @Schema(description = "代码块中文名", required = true)
    private String codeBlockName;

    @Schema(description = "代码块描述", required = false)
    private String codeBlockDescribe;

    @Schema(description = "代码块内容", required = true)
    private String codeBlockContent;

}
