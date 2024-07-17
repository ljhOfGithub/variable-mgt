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

import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "入参对象列表Vo")
public class OutSideParamsOutputVo implements Serializable {
    private static final long serialVersionUID = -7917995860842618476L;

    private Long id;

    @Schema(description = "对象名")
    private String objectName;

    @Schema(description = "对象中文名")
    private String objectLabel;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "数据来源")
    private VarProcessDataModelSourceType sourceType;

    @Schema(description = "来源表/外部服务")
    private String source;

    @Schema(description = "原始数据数")
    private Integer sourcepPropertyNum;

    @Schema(description = "扩展数据数")
    private Integer extendPropertyNum;

    @Schema(description = "创建部门")
    private String createdDeptName;
}
