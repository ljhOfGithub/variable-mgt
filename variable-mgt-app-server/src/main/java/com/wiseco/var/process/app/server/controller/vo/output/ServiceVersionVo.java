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
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 服务名称-版本号的实体类(Vo)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "服务名称-版本号的实体类Vo")
public class ServiceVersionVo implements Serializable {

    private static final long serialVersionUID = 4842758353359252087L;

    @Schema(description = "服务名称", required = true)
    private String serviceName;

    @Schema(description = "当前的启用版本号", required = true)
    private Integer version;

    @Schema(description = "产生过调用数据的版本号", required = true)
    private List<Integer> versions;
}
