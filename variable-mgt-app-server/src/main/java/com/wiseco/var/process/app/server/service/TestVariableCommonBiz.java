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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.var.process.app.server.controller.vo.input.TestVariableRulesInputDto;
import com.wiseco.var.process.app.server.enums.GenerateCustomEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestRules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangxianli
 * @since  2021/12/24
 */
@Service
@Slf4j
public class TestVariableCommonBiz {

    @Autowired
    private VarProcessTestRulesService varProcessTestVariableRulesService;

    /**
     * 获取在线自动生成表单及规则
     *
     * @param spaceId 空间ID
     * @return VarProcessTestRules List
     */

    public List<VarProcessTestRules> getRuleList(Long spaceId) {

        return varProcessTestVariableRulesService.list(
                new QueryWrapper<VarProcessTestRules>().lambda()
                        .eq(VarProcessTestRules::getVarProcessSpaceId, spaceId)
        );

    }

    /**
     * ruleSave
     * @param inputDto 入参
     */
    public void ruleSave(TestVariableRulesInputDto inputDto) {


        VarProcessTestRules testRules = new VarProcessTestRules();
        BeanUtils.copyProperties(inputDto, testRules);
        testRules.setVarProcessSpaceId(inputDto.getSpaceId());
        VarProcessTestRules one = varProcessTestVariableRulesService.getOne(new QueryWrapper<VarProcessTestRules>()
                .lambda()
                .eq(VarProcessTestRules::getVarProcessSpaceId, inputDto.getSpaceId())
                .eq(VarProcessTestRules::getVarPath, testRules.getVarPath())
                .eq(VarProcessTestRules::getVarType, testRules.getVarType())
        );
        if (one != null) {
            testRules.setId(one.getId());
        }
        varProcessTestVariableRulesService.saveOrUpdate(testRules);

    }

    /**
     * getFormCustomList
     * @return  GenerateCustomEnum
     */
    public Map<String, String> getFormCustomList() {

        Map<String, String> map = new LinkedHashMap<>();
        for (GenerateCustomEnum customEnum : GenerateCustomEnum.values()) {
            map.put(customEnum.getCode(), customEnum.getDesc());
        }

        return map;
    }

}
