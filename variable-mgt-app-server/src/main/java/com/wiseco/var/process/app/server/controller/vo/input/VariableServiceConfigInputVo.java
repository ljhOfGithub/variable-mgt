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

import com.wiseco.var.process.app.server.controller.vo.ServiceBasicConfigVo;
import com.wiseco.var.process.app.server.controller.vo.ServiceDataModelMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ServiceManifestMappingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiongzhewen
 * 实时服务保存配置时输入参数
 */

@Data
public class VariableServiceConfigInputVo implements Serializable {

    private static final long serialVersionUID = 8346948526380363852L;

    @Schema(description = "实时服务基本信息")
    private ServiceBasicConfigVo serviceBasicConfig;

    @Schema(description = "服务引用变量清单相关信息")
    private List<ServiceManifestMappingVo> serviceManifestMappings;

    @Schema(description = "手动添加的入参数据模型信息")
    private List<ServiceDataModelMappingVo> serviceDataModelMappings;

    @Schema(description = "流水号绑定")
    private String serialNumberBinding;
}
