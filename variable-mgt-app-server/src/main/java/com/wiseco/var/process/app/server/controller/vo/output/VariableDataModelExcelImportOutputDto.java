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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据模型 Excel 导入出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/4/26
 */
@Data
@Schema(description = "数据模型 Excel 导入输出参数")
public class VariableDataModelExcelImportOutputDto implements Serializable {

    private static final long serialVersionUID = 4658703533891557640L;

    @Schema(description = "Excel 文件导入情况反馈")
    private ExcelImportFeedback importFeedback;

    @Schema(description = "数据模型树形结构列表")
    private List<DomainDataModelTreeDto> dataModelTreeList;

    @Data
    @Schema(description = "Excel 文件导入情况反馈")
    public static class ExcelImportFeedback implements Serializable {

        private static final long serialVersionUID = 807951741631454430L;

        /**
         * 导入结果 TODO
         * key: , value:
         */
        @Schema(description = "导入结果")
        private Map<String, String> result;

        /**
         * 错误详情描述
         * key: 错误详情项目, value: 错误详情信息
         */
        @Schema(description = "错误详情描述", example = "['重复名单：100 条','格式有误：11 条，行号（不含表头）：5,8,15,16-27,345-567']")
        private List<Map<String, String>> failReason;
    }
}
