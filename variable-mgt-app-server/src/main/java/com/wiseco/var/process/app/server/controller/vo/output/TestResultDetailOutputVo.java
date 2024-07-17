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
import java.util.List;
import java.util.Map;

/**
 * 测试结果的详情(右侧)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "测试结果的详情(右侧)(控制层)")
public class TestResultDetailOutputVo implements Serializable {

    private static final long serialVersionUID = -6830925748619319687L;

    @Schema(description = "新的预期结果对比", example = "null")
    private List<Map<String, Object>> comparisonList;
}
