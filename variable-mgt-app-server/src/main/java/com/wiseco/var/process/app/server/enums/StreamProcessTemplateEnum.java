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
public enum StreamProcessTemplateEnum {

    /**
     * 流式加工变量模板
     */
    STATISTICAL_TEMPLATE("统计模板"),
    EVENT_TIME_DIFFERENCE_TEMPLATE("事件时间差模板"),
    BUSINESS_CHAIN_MATCHING_TEMPLATE("业务链匹配模板"),
    HISTORICAL_DATA_RETRIEVAL("历史数据取值"),
    CONCENTRATION_DAYS("集中度天数"),
    INDICATOR_FIELD_RANKING("判断指标-字段排行");

    private String desc;

    /**
     * 获取加工模板
     * @return list
     */
    public static List<SceneListSimpleOutputVO.ProcessTemplateOutputDto> findProcessTemplates() {
        return Arrays.stream(StreamProcessTemplateEnum.values()).map(item -> SceneListSimpleOutputVO.ProcessTemplateOutputDto.builder().processTemplate(item).build())
                .collect(Collectors.toList());
    }
}
