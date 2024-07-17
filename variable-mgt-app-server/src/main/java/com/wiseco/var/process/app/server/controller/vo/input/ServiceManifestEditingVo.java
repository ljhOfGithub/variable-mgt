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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServiceManifestEditingVo implements Serializable {
    private static final long serialVersionUID = -7917995867387438476L;

    @Schema(description = "变量清单信息")
    List<ServiceManifestInfo> manifests;
    @Schema(description = "手动添加(manual为1) 的数据模型id列表")
    List<Long> dataModels;

    @Data
    public static class ServiceManifestInfo {
        private static final long serialVersionUID = -7917995867364618476L;

        @Schema(description = "清单id", required = true)
        private Long manifestId;

        @Schema(description = "角色：1-主清单；0：异步加工清单", required = true)
        private Short role;

        @Schema(description = "true:立即生效", required = true)
        private Boolean immediateEffect;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @Schema(description = "生效时间")
        private Date validTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @Schema(description = "失效时间")
        private Date invalidTime;

        @Schema(description = "执行总笔数")
        private Long totalExecuteCount;
    }
}
