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

import com.wiseco.boot.data.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量异常值配置创建入参 DTO
 *
 * @author kangyankun
 * @since 2022/8/31
 */

@EqualsAndHashCode(callSuper = true)
@Schema(description = "变量异常值保存DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableConfigExceptionValueQueryInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", required = true, example = "1")
    private Long varProcessSpaceId;

    @Schema(description = "数据类型", required = false, example = "int、double、string、boolean、date、datetime")
    private String dataType;

    @Schema(description = "异常值", required = false, example = "NA,-9999")
    private String exceptionValue;

    @Schema(description = "异常类型", required = false, example = "1:内置异常 2:自定义")
    private Integer exceptionType;

    @Schema(description = "异常说明", required = false, example = "异常说明")
    private String exceptionExplain;

}
