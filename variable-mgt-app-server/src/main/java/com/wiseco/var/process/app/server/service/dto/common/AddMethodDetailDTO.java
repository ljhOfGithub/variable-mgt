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

import java.io.Serializable;
import java.util.List;

/**
 * @author: fudengkui
 * @since : 2023-02-23 17:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "添加方法DTO")
public class AddMethodDetailDTO implements Serializable {

    @Schema(description = "方法列表", required = true, type = "List", example = "[]")
    private List<MethodDTO> methods;

    @Schema(description = "属性列表", required = true, type = "List", example = "[]")
    private List<AttributeDTO> attributes;

}
