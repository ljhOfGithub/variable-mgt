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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 属性面板对象标题
 * @Author: xiewu
 * @Date: 2021/11/10
 * @Time: 11:37
 */
@AllArgsConstructor
@Getter
public enum PropertyPanelTitleEnum {

    /**
     * 决策领域属性，基本信息标题
     */
    DOMAIN_BASIC_TITLE("基本信息"),

    /**
     * 方案属性信息, 使用的随机数信息
     */
    ABTEST_PLAN_USE_TITLE("使用的随机数信息");

    private String message;
}
