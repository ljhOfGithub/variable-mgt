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
package com.wiseco.var.process.app.server.enums.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: TODO
 * @Author: zhouxiuxiu
 * @Date: 2022/6/16 16:44
 */
@Getter
@AllArgsConstructor
public enum TemplateUnitTypeEnum {

    //区分查询表
    STRATEGY_COMPONENT(1,"策略组件"),
    SPACE_VARIABLE(2,"空间变量"),
    SPACE_COMMON_FUNCTION(3,"空间公共函数"),;

    private final Integer type;
    private final String description;

    /**
     * getTypeEnum
     * @param type Integer
     * @return TemplateUnitTypeEnum
     */
    public static TemplateUnitTypeEnum getTypeEnum(Integer type) {
        for (TemplateUnitTypeEnum templateUnitTypeEnum : TemplateUnitTypeEnum.values()) {
            if (templateUnitTypeEnum.getType().equals(type)) {
                return templateUnitTypeEnum;
            }
        }
        return null;
    }
}
