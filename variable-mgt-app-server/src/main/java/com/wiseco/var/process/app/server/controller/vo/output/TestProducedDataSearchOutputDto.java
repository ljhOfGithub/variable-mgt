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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 决策数据查询出参
 *
 * @author: wangxianli
 */
@Schema(description = "生产数据查询出参DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestProducedDataSearchOutputDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表头")
    private JSONObject tableHeaderField;

    @Schema(description = "测试明细")
    private List<String> dataList;

    @Schema(description = "总记录数", example = "0")
    private Long totalNums;

    @Schema(description = "每页显示条数", example = "50")
    private Long totSize;

    @Schema(description = "当前页码", example = "1")
    private Long pageNum;

}
