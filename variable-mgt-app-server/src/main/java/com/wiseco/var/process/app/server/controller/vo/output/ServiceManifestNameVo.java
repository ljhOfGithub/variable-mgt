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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 服务名称(版本号)-变量清单的实体类(Vo)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "服务名称(版本号)-变量清单的实体类Vo")
@EqualsAndHashCode
public class ServiceManifestNameVo implements Serializable {

    private static final long serialVersionUID = 1026172259065191208L;

    @Schema(description = "实时服务的Id", example = "10000")
    private Long serviceId;

    @Schema(description = "变量清单的Id", example = "20000")
    private Long manifestId;

    @Schema(description = "组合名称", example = "实时服务A(1)-变量清单A")
    private String name;
}
