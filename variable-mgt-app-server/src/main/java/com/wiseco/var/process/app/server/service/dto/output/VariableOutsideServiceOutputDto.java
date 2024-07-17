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
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author: wangxianli
 * @since: 2022/9/14
 */
@Schema(description = "变量引用外部服务输出DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableOutsideServiceOutputDto implements Serializable {
    private static final long serialVersionUID = 8668690652316747092L;

    @Schema(description = "外部服务主键", example = "1")
    private Long outsideServiceId;

    /**
     * 外部服务名称
     */
    @Schema(description = "外部服务名称", example = "FICO分")
    private String outsideServiceName;

    /**
     * 外部服务编码
     */
    @Schema(description = "外部服务编码", example = "1001")
    private String outsideServiceCode;

    /**
     * 服务入参映射
     */
    @Schema(description = "服务入参映射")
    private List<OutsideServiceVarDto> outsideServiceVarList;

    /**
     * 服务出参类型
     */
    @Schema(description = "服务出参类型")
    private OutsideServiceRefObjectDto outsideServiceRefObjectDto;

    @Data
    @SuperBuilder
    @Schema(description = "数据模型引用信息")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutsideServiceVarDto {
        @Schema(description = "外部服务主键", example = "1")
        private Long outsideServiceId;

        @Schema(description = "变量名称", example = "name")
        private String varName;

        @Schema(description = "参数名称", example = "客户名称")
        private String description;

        @Schema(description = "数据类型", example = "string")
        private String fieldType;

        @Schema(description = "是否数组 1是，0否", example = "1")
        private String isArr;
    }

    @Data
    @SuperBuilder
    @Schema(description = "接收对象信息")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutsideServiceRefObjectDto {

        @Schema(description = "接收对象名称", example = "AppFico")
        private String refObjectName;

        @Schema(description = "接收对象名称中文名称", example = "AppFico中文")
        private String refObjectNameCn;

        @Schema(description = "是否使用根对象: 1:是 0:否", example = "1")
        private Integer useRootObjectFlag;

    }
}
