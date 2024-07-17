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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通过外数服务获取对应类型的数据模型 DTO")
public class OutsideServerGetDataModelOutputVO implements Serializable {

    @Schema(description = "数据模型ID", required = true, example = "1")
    private Long dataModelId;

    @Schema(description = "对象名称")
    private String objectName;

    @Schema(description = "对象中文名")
    private String objectLabel;

    @Schema(description = "版本")
    private Integer version;

    @Schema(description = "引擎使用变量")
    private OutParameterBinding outParameterBinding;

    @Data
    public static class OutParameterBinding {

        private String cnName;

        private int isArr;

        private String mapping;

        private String name;

        private String type;

        private int useRootObjectFlag;
    }

}