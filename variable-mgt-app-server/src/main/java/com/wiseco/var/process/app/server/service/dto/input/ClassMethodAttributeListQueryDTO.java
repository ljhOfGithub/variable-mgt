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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: fudengkui
 * @since: 2023-02-21 20:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "jar包class的方法/属性列表DTO")
public class ClassMethodAttributeListQueryDTO implements Serializable {

    @Schema(description = "类编号", required = true, type = "List", example = "[]")
    private List<String> classIdentifiers;

}
