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

import com.wiseco.decision.engine.var.runtime.core.Engine;
import com.wiseco.decision.engine.var.transform.component.compiler.dataBuilder.vo.VarVo;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wisecotech.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author wangxianli
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestExecuteInputDto {

    private JSONObject inputHeader;
    private JSONObject expectedHeader;
    private JSONObject resultHeader;
    private JSONObject dataModelHeaderDto;
    private Map<String, TestFormDto> inputVarMap;
    private List<String> outputExcludeVarList;

    private Engine engine;

    private VarVo varVo;

    /**
     * 组件测试批号
     */
    private String batchNo;

    /**
     * 测试集Id
     */
    private Long testId;

    /**
     * 测试结果表Id
     */
    private Long resultId;

    /**
     * 返回值key
     */
    private String returnKey;

    /**
     *  外数服务的调用方式
     */
    private Map<String, String> outsideServiceStrategyMap;

}
