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
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: xiewu
 */
@Data
@Schema(description = "导入字典项 出参DTO")
public class DictDetailImportOutputDto {

    @Schema(description = "导入结果", example = "")
    private Map<String, String> result;
    @Schema(description = "错误详情描述", example = "['重复名单：100 条','格式有误：11 条，行号（不含表头）：5,8,15,16-27,345-567']")
    private List<Map<String, String>> failReason;

}
