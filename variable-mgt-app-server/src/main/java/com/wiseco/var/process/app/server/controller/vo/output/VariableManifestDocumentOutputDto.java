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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 变量清单文档出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "服务接口文档出参")
public class VariableManifestDocumentOutputDto {

    @Schema(description = "基本信息")
    private BasicInfoVo basicInfo;

    @Schema(description = "请求参数")
    private List<DomainDataModelTreeDto> requestStructure;

    @Schema(description = "请求示例")
    private JSONObject requestSample;

    @Schema(description = "请求示例字符串")
    private String requestSampleStr;

    @Schema(description = "返回结果")
    private List<DomainDataModelTreeDto> responseStructure;

    @Schema(description = "返回示例")
    private JSONObject responseSample;

    @Schema(description = "返回状态码")
    private List<ResponseCodeVo> responseCode;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "接口文档 - 基本信息")
    public static class BasicInfoVo implements Serializable {

        private static final long serialVersionUID = -1703766481043730480L;

        @Schema(description = "接口名称")
        private String name;

        @Schema(description = "接口类型")
        private String type;

        @Schema(description = "数据格式")
        private String dataFormat;

        @Schema(description = "请求地址")
        private String url;

        @Schema(description = "请求方式")
        private String requestMethod;

        @Schema(description = "通讯协议")
        private String protocol;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "接口文档 - 返回状态码")
    public static class ResponseCodeVo implements Serializable {

        private static final long serialVersionUID = 6732020452613329961L;

        @Schema(description = "状态码类型", example = "error_code")
        private String type;

        @Schema(description = "状态码", example = "001")
        private String code;

        @Schema(description = "说明", example = "服务调用流水号使用的变量没有传值")
        private String description;
    }
}
