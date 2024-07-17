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
package com.wiseco.var.process.app.server.service.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据领域id查询策略参数列表
 *
 * @author: zhouxiuxiu
 * @since: 2021/11/8 14:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "根据数参数名称查询系统参数 出参DTO")
public class SysParamByParamNameListOutputDto {

    @Schema(description = "主键id", example = "1")
    private Long id;

    @Schema(description = "参数名", example = "initPwd")
    private String paramName;

    @Schema(description = "参数中文名", example = "用户初始密码")
    private String paramNameCn;

    @Schema(description = "数据类型：int、double、string、boolean、date、datetime", example = "string")
    private String dataType;

    @Schema(description = "参数类型：1内置参数 2自定义参数", example = "2")
    private Integer paramType;

    @Schema(description = "参数值", example = "123456")
    private String paramValue;

}
