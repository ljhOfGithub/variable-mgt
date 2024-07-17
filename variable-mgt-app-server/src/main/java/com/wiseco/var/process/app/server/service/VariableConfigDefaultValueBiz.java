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
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigDefaultValueCheckInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigDefaultValueInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigDefaultValueQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableConfigDefaultValueOutputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.service.common.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author kangyankun
 */
@Slf4j
@Service
public class VariableConfigDefaultValueBiz {

    @Autowired
    private VarProcessConfigDefaultService varProcessConfigDefaultValueService;

    @Autowired
    private UserService userService;

    /**
     * 更新默认配置的值
     * @param inputDto 输入实体类对象
     * @return 更新默认配置的值的结果
     */
    public Boolean updateConfigDefaultValue(VariableConfigDefaultValueInputDto inputDto) {
        VarProcessConfigDefault value = new VarProcessConfigDefault();
        BeanUtils.copyProperties(inputDto, value);
        value.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        value.setDefaultValue(ObjectUtils.isEmpty(inputDto.getDefaultValue()) ? "" : inputDto.getDefaultValue());
        value.setDefaultValue(ObjectUtils.isEmpty(inputDto.getDefaultValue()) ? "" : inputDto.getDefaultValue());
        value.setUpdatedTime(new Date());
        return varProcessConfigDefaultValueService.updateById(value);
    }

    /**
     * 获取默认配置的值的列表
     * @param inputDto 输入实体类对象
     * @return 默认配置的值的列表
     */
    public List<VariableConfigDefaultValueOutputDto> getConfigDefaultValueList(VariableConfigDefaultValueQueryInputDto inputDto) {
        List<VarProcessConfigDefault> varProcessConfigDefaultList = varProcessConfigDefaultValueService.list(new QueryWrapper<VarProcessConfigDefault>().lambda()
                .eq(VarProcessConfigDefault::getVarProcessSpaceId, inputDto.getVarProcessSpaceId()));

        List<VariableConfigDefaultValueOutputDto> outputDtoList = new ArrayList<>();

        List<String> userName = varProcessConfigDefaultList.stream().flatMap(item -> Stream.of(item.getUpdatedUser())).distinct().collect(Collectors.toList());
        Map<String, String> fullNameMap = userService.findFullNameMapByUserNames(userName);
        for (VarProcessConfigDefault varProcessConfigDefault : varProcessConfigDefaultList) {
            VariableConfigDefaultValueOutputDto outputDto = new VariableConfigDefaultValueOutputDto();
            BeanUtils.copyProperties(varProcessConfigDefault, outputDto);
            outputDto.setUpdatedUser(fullNameMap.get(varProcessConfigDefault.getUpdatedUser()));
            outputDto.setUpdatedTime(DateUtil.parseDateToStr(varProcessConfigDefault.getUpdatedTime(), DateUtil.FORMAT_LONG));
            outputDtoList.add(outputDto);
        }

        //按照指定顺序进行排序
        outputDtoList.sort(new Comparator<VariableConfigDefaultValueOutputDto>() {
            private final Map<String, Integer> datatypeOrder = new HashMap<>(8);

            {
                // 初始化datatype的顺序
                datatypeOrder.put("string", 1);
                datatypeOrder.put("int", MagicNumbers.TWO);
                datatypeOrder.put("double", MagicNumbers.THREE);
                datatypeOrder.put("date", MagicNumbers.FOUR);
                datatypeOrder.put("datetime", MagicNumbers.FIVE);
                datatypeOrder.put("boolean", MagicNumbers.SIX);
            }

            @Override
            public int compare(VariableConfigDefaultValueOutputDto o1, VariableConfigDefaultValueOutputDto o2) {
                // 获取o1和o2的datatype字段值
                String datatype1 = o1.getDataType();
                String datatype2 = o2.getDataType();

                // 根据datatype字段的顺序比较
                int order1 = datatypeOrder.getOrDefault(datatype1, Integer.MAX_VALUE);
                int order2 = datatypeOrder.getOrDefault(datatype2, Integer.MAX_VALUE);

                return Integer.compare(order1, order2);
            }
        });
        return outputDtoList;
    }

    /**
     * 检查是否存在
     * @param inputDto 输入实体类对象
     * @return 检查是否存在的结果
     */
    public Boolean checkExist(VariableConfigDefaultValueCheckInputDto inputDto) {
        List<VarProcessConfigDefault> varProcessConfigDefaultList = varProcessConfigDefaultValueService.list(new QueryWrapper<VarProcessConfigDefault>().lambda()
                .eq(VarProcessConfigDefault::getVarProcessSpaceId, inputDto.getVarProcessSpaceId()));
        for (VarProcessConfigDefault varProcessConfigDefault : varProcessConfigDefaultList) {
            if (inputDto.getDataType().equalsIgnoreCase(varProcessConfigDefault.getDataType()) && !StringUtils.isEmpty(varProcessConfigDefault.getDefaultValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取缺失值map
     * @return map
     */
    public Map<String, String> getDefaultValueMap() {
        HashMap<String, String> map = new HashMap<>(MagicNumbers.EIGHT);
        VariableConfigDefaultValueQueryInputDto inputDto = new VariableConfigDefaultValueQueryInputDto();
        inputDto.setVarProcessSpaceId(1L);
        List<VariableConfigDefaultValueOutputDto> list = getConfigDefaultValueList(inputDto);
        list.forEach(item -> map.put(item.getDataType(),item.getDefaultValue()));
        return map;
    }
}
