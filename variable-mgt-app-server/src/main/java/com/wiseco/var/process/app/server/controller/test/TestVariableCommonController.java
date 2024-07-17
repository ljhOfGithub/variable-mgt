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
package com.wiseco.var.process.app.server.controller.test;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.TestVariableRulesInputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestRules;
import com.wiseco.var.process.app.server.service.TestVariableCommonBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author wangxianli
 */
@RestController
@RequestMapping("/testVariableCommon")
@Slf4j
@Tag(name = "变量测试(公共)")
public class TestVariableCommonController {
    @Autowired
    private TestVariableCommonBiz testCommonBiz;

    /**
     * 获取生成规则配置
     *
     * @param spaceId spaceId
     * @return 测试规则的list
     */
    @GetMapping("/ruleList/{spaceId}")
    @Operation(summary = "获取生成规则配置")
    public APIResult<List<VarProcessTestRules>> getRuleList(@PathVariable("spaceId") Long spaceId) {
        return APIResult.success(testCommonBiz.getRuleList(spaceId));

    }

    /**
     * 保存规则配置
     *
     * @param inputDto inputDto
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/saveRule")
    @Operation(summary = "保存规则配置")
    public APIResult ruleSave(@RequestBody TestVariableRulesInputDto inputDto) {
        testCommonBiz.ruleSave(inputDto);
        return APIResult.success();

    }

    /**
     * TestVariableCommonController
     *
     * @return 自定义列表
     */
    @GetMapping("/formCustomList")
    @Operation(summary = "获取自定义列表")
    public APIResult<Map<String, String>> getFormCustomList() {
        return APIResult.success(testCommonBiz.getFormCustomList());
    }

}
