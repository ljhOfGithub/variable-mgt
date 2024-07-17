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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 测试数据明细响应DTO
 *
 * @author wangxianli
 * @since 2021/11/30 19:40
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "测试数据明细响应DTO")
public class TestResultsOutputDto implements Serializable {

    private static final long serialVersionUID = 8668690652316747092L;

    @Schema(description = "UUID,在线自动生成和导入模板使用", example = "1")
    private String uuid;

    @Schema(description = "测试明细表头", example = "无")
    private Map<String, Object> tableHeaderField;

    @Schema(description = "新的测试数据明细, 只有序号、流水号、耗时和状态", example = "null")
    private List<Map<String, Object>> newDataList;

    @Schema(description = "总记录数", example = "0")
    private Integer totalNums;

    @Schema(description = "测试集数据", example = "null")
    private TestCollectOutputDto testCollect;

}
