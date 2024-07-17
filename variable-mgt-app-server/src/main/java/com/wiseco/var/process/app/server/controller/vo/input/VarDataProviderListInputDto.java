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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 模板静态接口
 *
 * @author wiseco
 */
@Schema(description = "模板动态DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VarDataProviderListInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "data_provider名字", required = true, example = "data_provider_number")
    private String providerName;

    @Schema(description = "类型 varProcess:变量 commonFunction:公共函数", example = "1")
    private String type;

    @Schema(description = "空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量ID", required = true, example = "1")
    private Long variableId;

    @Schema(description = "函数ID", required = true, example = "1")
    private Long functionId;

    @Schema(description = "数据类型 string,number,boolean,date", example = "1")
    private String dataType;

    @Schema(description = "追加变量路径，数组循环内的需要有", example = "[\"input.application.age\",\"input.application.age\"]")
    private List<String> dataValues;

    @Schema(description = "当前会话id:保持本地变量/参数时返回的sessionid", example = "A100001")
    private String sessionId;

    @Schema(description = "自定义函数返回数据类型：int、double、string、boolean、date、datetime", example = "1")
    private String returnType;

    @Schema(description = "查询字典值时需要传入", example = "input.application.age")
    private String dictValue;

    @Schema(description = "this字段对应的全路径", required = true, example = "input.contactInfo")
    private String dictFullPathValue;
}
