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

import com.wiseco.var.process.app.server.controller.vo.ServiceDataModelMappingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
@Schema(description = "变量清单的详细信息map & 数据模型/外部入参引用信息")
public class ManifestAndDataModelInfoVo implements Serializable {
    private static final long serialVersionUID = -7917995868564618476L;

    @Schema(description = "变量清单详情信息")
    private List<ServiceManifestDetailOutputVo> manifestDetailVos;

    @Schema(description = "相关数据模型信息")
    private List<ServiceDataModelMappingVo> dataModels;
}
