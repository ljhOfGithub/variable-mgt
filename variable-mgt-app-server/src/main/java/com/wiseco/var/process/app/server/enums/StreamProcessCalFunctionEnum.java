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
package com.wiseco.var.process.app.server.enums;

import com.wiseco.var.process.app.server.controller.vo.SceneListSimpleOutputVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum StreamProcessCalFunctionEnum {
    /**
     * 流式变量 计算函数 枚举
     */
    FREQUENCY_COUNTING("次数统计"),
    DISTINCT_COUNTING("去重计数"),
    CONSECUTIVE_OCCURRENCE_COUNT("连续出现次数"),
    SUMMATION("求和"),
    MEAN("平均值"),
    MAXIMUM("最大值"),
    MINIMUM("最小值"),
    VARIANCE("方差"),
    STANDARD_DEVIATION("标准差");

    private String desc;

    /**
     * 获取计算函数
     * @return list
     */
    public static List<SceneListSimpleOutputVO.CalculateFunctionOutputDto> findCalculateFunctions() {
        return Arrays.stream(StreamProcessCalFunctionEnum.values()).map(item -> SceneListSimpleOutputVO.CalculateFunctionOutputDto.builder().calculateFunction(item).build())
                .collect(Collectors.toList());
    }
}
