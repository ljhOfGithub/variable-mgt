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

import com.wiseco.boot.commons.data.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceVersionListInputVO extends PageDTO {

    @Schema(description = "场景：1-非停用版本；2-全部版本",allowableValues = {"1","2"})
    private Integer scene;

    @Schema(description = "服务id")
    @NotNull(message = "请传入服务id")
    private Long serviceId;

    @Schema(description = "变量清单ID")
    private Long manifestId;

    @Schema(description = "部门code")
    private String deptCode;

}
