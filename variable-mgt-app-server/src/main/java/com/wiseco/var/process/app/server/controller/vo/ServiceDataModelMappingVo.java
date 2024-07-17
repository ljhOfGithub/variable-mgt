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
package com.wiseco.var.process.app.server.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "服务中选择的变量清单引用的数据模型信息")
public class ServiceDataModelMappingVo implements Serializable {
    private static final long SERIAL_VERSION_UID = 8759865846955173992L;

    private Long id;

    @Schema(description = "对象名")
    private String name;

    @Schema(description = "对象中文名")
    private String label;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "是否依赖：o-否；1-是")
    private Integer dependent;

    @Schema(description = "变量清单带出/手动添加：0-变量清单带出；1-手动添加")
    private Integer manual;
}
