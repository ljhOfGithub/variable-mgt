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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "流程-变量查询DTO")
public class VariableFlowQueryDto {
    private Long spaceId;
    private Long manifestId;
    private String keywords;
    private String deptCode;
    private String dataType;
    private List<Long> categoryIds;
    /**
     * 变量标签搜索关键字
     */
    private List<String> tagNames;
    private String sortKey;
    private String sortMethod;
    private List<String> excludeList;
}
