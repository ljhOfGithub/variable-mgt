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

import com.wiseco.var.process.app.server.commons.enums.ServiceMsgFormatEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "实时服务list出参")
public class RestServiceListOutputVO {

    @Schema(description = "服务id",example = "5000")
    private Long serviceId;

    @Schema(description = "服务名称",example = "实时服务")
    private String serviceName;

    @Schema(description = "服务编码",example = "serviceCode")
    private String serviceCode;

    @Schema(description = "接口地址",example = "/abc/asd")
    private String url;

    @Schema(description = "服务分类id",example = "123")
    private Long serviceCategoryId;

    @Schema(description = "服务分类名",example = "服务默认分类")
    private String serviceCategoryName;

    @Schema(description = "是否开启trace",example = "true")
    private Boolean enableTrace;

    @Schema(description = "报文格式",allowableValues = {"JSON","XML"})
    private ServiceMsgFormatEnum messageFormat;

    @Schema(description = "版本数",example = "2")
    private Integer versionCount;

    @Schema(description = "最大版本号",example = "4")
    private Integer maxVersion;

//    @Schema(description = "版本列表")
//    private List<VersionInfo> versionList;

    @Schema(description = "是否存在发布过的版本",example = "true")
    private Boolean hasAlreadyDeploy;

    @Schema(description = "是否存在启用版本",example = "false")
    private Boolean hasEnabled;

}
