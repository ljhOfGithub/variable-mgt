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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@Schema(description = "测试数据表头Dto")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TestTableHeaderDto implements Serializable {

    @Schema(description = "索引", example = "input.application")
    private String index;

    @Schema(description = "变量全路径", example = "input.application.channelNo")
    private String name;

    @Schema(description = "变量名称", example = "属性信息")
    private String label;

    @Schema(description = "是否数组", example = "0")
    private int isArr;

    @Schema(description = "数据类型", example = "type")
    private String type;

    @Schema(description = "字段类别：0-输入，1-预期结果，2-实际结果", example = "0")
    private int fieldType;

}
