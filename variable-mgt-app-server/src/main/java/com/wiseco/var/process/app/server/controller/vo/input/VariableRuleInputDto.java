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

import com.wiseco.var.process.app.server.controller.vo.PlanResultVo;
import com.wiseco.var.process.app.server.enums.HandleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 生成变量 DTO
 *
 * @author chenzhuang
 */
@Schema(description = "生成变量 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableRuleInputDto implements Serializable {

    private static final long   serialVersionUID = 8799865908944973993L;
    @Schema(description = "生成变量id")
    private Long                id;

    @Schema(description = "操作类型")
    private HandleEnum          handleEnum;

    @Schema(description = "变量模版id")
    private Long                functionId;

    @Size(max = 100, message = "变量名称生成规则字数限制100")
    @NotNull(message = "变量名称生成规则不能为空")
    @Schema(description = "变量名称生成规则'")
    private String              nameRule;

    @Size(max = 100, message = "变量编码生成规则字数限制100")
    @NotNull(message = "变量编码生成规则不能为空")
    @Schema(description = "变量编码生成规则'")
    private String              identifierRule;

    @NotNull(message = "设置变量分类不能为空")
    @Schema(description = "变量分类")
    private Long                variableType;

    @Schema(description = "数据类型：string、int、double、date、datetime、boolean")
    private String              dataType;

//    @Schema(description = "生成变量数据")
//    private List<ProduceRecord> produceRecordList;

    @Schema(description = "生成变量数据(全部方案的全量数据)")
    private List<PlanResultVo> variableProduceResult;

//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Schema(description = "生成变量数据")
//    public static class ProduceRecord {
//        @Schema(description = "明细记录id")
//        private Long   id;
//        @Schema(description = "方案名")
//        private String planName;
//        @NotNull(message = "变量名称不能为空")
//        @Size(max = 100, message = "变量名称不能超过 100 个字符")
//        @Schema(description = "变量名称")
//        private String name;
//        @NotNull(message = "变量编号不能为空")
//        @Size(max = 100, message = "变量编号不能超过 100 个字符")
//        @Schema(description = "变量编号")
//        private String identifier;
//
//        @Schema(description = "参数内容")
//        private String paramJson;
//    }

}
