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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 操作按钮
 *
 * @author: liaody
 * @since: 2021/11/8 15:01
 */
@Data
@Builder
@Schema(description = "操作按钮")
@AllArgsConstructor
@NoArgsConstructor
public class OperationButton implements Serializable {
    private static final long serialVersionUID = 181679563138063340L;

    @Schema(description = "修改", example = "edit")
    private String value;

    @Schema(description = "修改属性", example = "修改")
    private String label;
}
