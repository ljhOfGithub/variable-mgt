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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 根据领域id查询策略参数列表
 *
 * @author: zhouxiuxiu
 * @since: 2021/11/8 14:25
 */
@Data
@Schema(description = "根据数据和参数类型查询系统参数 入参DTO")
public class SysParamByDataAndParamTypeListInputDto {

    @Schema(description = "数据类型：int、double、string、boolean、date、datetime", example = "string")
    @NotEmpty(message = "数据类型不能为空！")
    private String dataType;

    @Schema(description = "参数类型：1内置参数 2自定义参数", example = "2")
    @NotNull(message = "参数类型不能为空！")
    private Integer paramType;

}
