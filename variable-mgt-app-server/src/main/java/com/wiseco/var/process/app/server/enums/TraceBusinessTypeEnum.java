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
 * @author wuweikang
 */
@Getter
@AllArgsConstructor
public enum TraceBusinessTypeEnum {
    /**
     * 清单测试
     */
    MANIFEST_TEST("清单测试"),
    /**
     * 批量回溯
     */
    BACKTRACKING("批量回溯"),
    /**
     * 实时服务
     */
    REST_SERVICE("实时服务");

    String desc;
}
