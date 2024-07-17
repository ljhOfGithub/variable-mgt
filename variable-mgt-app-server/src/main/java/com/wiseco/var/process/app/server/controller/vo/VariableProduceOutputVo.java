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
package com.wiseco.var.process.app.server.controller.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量上架 DTO
 *
 * @author wangxianli
 */
@Schema(description = "生成变量 vo")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableProduceOutputVo implements Serializable {

    private static final long serialVersionUID = 8799865908944973993L;
    @Schema(description = "生成变量")
    private Long id;

    @Schema(description = "变量模版id")
    private Long functionId;

    @Schema(description = "变量名称生成规则'")
    private String nameRule;

    @Schema(description = "变量编码生成规则'")
    private String identifierRule;

    @Schema(description = "变量分类")
    private String variableType;

    @Schema(description = "生成变量数据")
    @JsonProperty("variableRuleRecordIPage")
    private IPage<VariableProduceRecordVo> variableRuleRecordIpage;

}
