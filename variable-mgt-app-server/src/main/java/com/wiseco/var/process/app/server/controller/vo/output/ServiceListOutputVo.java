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
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "服务及版本号信息")
public class ServiceListOutputVo implements Serializable {
    private static final long serialVersionUID = 4111201259607255989L;

    @Schema(description = "服务编码")
    private String                      serviceCode;

    @Schema(description = "服务名称")
    private String                      serviceName;

    @Schema(description = "[版本号: 服务id, …]")
    private List<Map<Integer, Long>>    calledVersions;
}
