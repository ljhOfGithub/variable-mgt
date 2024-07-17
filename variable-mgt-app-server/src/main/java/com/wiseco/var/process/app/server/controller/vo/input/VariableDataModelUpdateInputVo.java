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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 变量空间数据模型修改 入参 Vo
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/14
 */
@Data
@Schema(description = "变量空间数据模型修改输入参数")
public class VariableDataModelUpdateInputVo implements Serializable {

    private static final long serialVersionUID = -7287521268334160923L;
    @Schema(description = "上一步信息")
    VariableDataModelAddNewNextInputVo firstPageInfo;
    @Schema(description = "变量空间 ID", example = "1")
    private Long spaceId;
    @Schema(description = "数据模型 ID", example = "90000000")
    private Long dataModelId;
    @Schema(description = "数据结构定义, 这个是用来保存第二步的数据的JSON")
    private DomainDataModelTreeDto content;

}
