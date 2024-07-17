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

/**
 * 变量类别出参 DTO
 *
 * @author kangyankun
 * @since 2022/8/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量类别输出参数")
public class VariableConfigExceptionValueOutputDto implements Serializable {

    private static final long serialVersionUID = 8240823771891283410L;

    @Schema(description = "主键id", example = "1")
    private Long id;

    @Schema(description = "变量空间ID", example = "10000")
    private Long varProcessSpaceId;

    @Schema(description = "异常类型", example = "1:内置异常 2:自定义")
    private Integer exceptionType;

    @Schema(description = "异常说明", example = "int、double、string、boolean、date、datetime")
    private String exceptionExplain;

    @Schema(description = "数据类型", example = "int、double、string、boolean、date、datetime")
    private String dataType;

    @Schema(description = "异常值", required = true, example = "NA,-9999")
    private String exceptionValue;

    @Schema(description = "最后编辑人", required = true, example = "NA,-9999")
    private String updatedUser;

    @Schema(description = "编辑时间", required = true, example = "2022-08-31 10:00:0")
    private String updatedTime;

}
