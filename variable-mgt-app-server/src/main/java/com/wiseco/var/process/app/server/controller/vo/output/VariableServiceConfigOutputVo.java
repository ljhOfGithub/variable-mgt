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

import com.wiseco.var.process.app.server.controller.vo.ServiceBasicConfigVo;
import com.wiseco.var.process.app.server.controller.vo.ServiceDataModelMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ServiceManifestMappingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "实时服务版本详情-配置信息输出参数")
public class VariableServiceConfigOutputVo implements Serializable {
    private static final long serialVersionUID = -7917995372842618791L;

    @Schema(description = "实时服务基本信息")
    private ServiceBasicConfigVo serviceBasicConfig;

    @Schema(description = "服务引用变量清单相关信息")
    private List<ServiceManifestMappingVo> serviceManifestMappings;

    @Schema(description = "数据模型 & 手动添加的入参对象 相关信息")
    private List<ServiceDataModelMappingVo> serviceDataModelMappings;

    @Schema(description = "流水号绑定")
    private String serialNumberBinding;

    @Schema(description = "code,name和category是否可以更改——true为可以更改,false为不可以更改")
    private Boolean isEdit;
}
