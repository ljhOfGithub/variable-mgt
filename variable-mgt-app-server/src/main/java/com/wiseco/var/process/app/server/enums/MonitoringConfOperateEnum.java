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
 * 监控预警规则操作枚举
 *
 * @author wuweikang
 */
@AllArgsConstructor
@Getter
public enum MonitoringConfOperateEnum {
    /**
     * 启用
     */
    UP("启用"),
    /**
     * 停用
     */
    DOWN("停用");

    /**
     * 详情
     */
    private final String desc;
}
