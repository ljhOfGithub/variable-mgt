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
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 测试表单格式化报文入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "测试表单格式化报文输入参数")
public class TestFormDatagramFormatInputDto implements Serializable {

    private static final long serialVersionUID = -6289749986132401174L;

    @Schema(description = "报文")
    private String datagram;

    @Schema(description = "目标格式", example = "json, xml")
    @NotBlank(message = "目标格式不能为空")
    private String targetFormat;
}