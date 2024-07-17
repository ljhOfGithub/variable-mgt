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
 * 告警等级枚举
 *
 * @author wuweikang
 */
@Getter
@AllArgsConstructor
public enum AlertGradeEnum {

    /**
     * 轻微
     */
    SLIGHT("轻微"),
    /**
     * 一般
     */
    GENERAL("一般"),
    /**
     * 严重
     */
    SERIOUS("严重");

    /**
     * 描述
     */
    private final String desc;

}
