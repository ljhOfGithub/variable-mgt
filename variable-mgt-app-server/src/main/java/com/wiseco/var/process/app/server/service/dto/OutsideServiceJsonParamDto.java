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
package com.wiseco.var.process.app.server.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: xiewu
 * @since: 2021/12/29 14:54
 */
@Data
@Schema(description = "外部服务请求参数配置的json参数Dto")
public class OutsideServiceJsonParamDto {

    @Schema(description = "请求参数类型：header参数，body参数，url参数", example = "header参数")
    @JsonProperty("request_type")
    private String requestType;

    @Schema(description = "参数名", example = "clientID")
    @JsonProperty("param_name")
    private String paramName;

    @Schema(description = "参数中文名", example = "接口调用账号")
    @JsonProperty("param_desc")
    private String paramDesc;

    @Schema(description = "字段类型：string,int,double,date,datetime", example = "string")
    @JsonProperty("param_type")
    private String paramType;

    @Schema(description = "加密方式：无,SHA256,MD5,SM3", example = "SHA256")
    private String encryption;

    @Schema(description = "参数取值方式", example = "19", allowableValues = "变量传入:varIncom，固定值:fixedValue，系统参数:sysParam，系统方法:sysMethod")
    @JsonProperty("value_type")
    private String valueType;

    @Schema(description = "固定值/系统参数/系统方法", example = "")
    @JsonProperty("param_value")
    private String paramValue;
}
